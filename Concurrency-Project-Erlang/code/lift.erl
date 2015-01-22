-module(lift).

-export([newLift/1]).

-
import(insert,[insert/4]).
-import(waitTime,[waitTime/4]).
-import(stopAt,[stopAt/3]).

%liftProcess({accept, Floor, _FloorAgent, Dir}, {Now, Stoplist, N}) ->
%  NewStoplist = insert(Stoplist, Now, Dir, Floor),
%  io:format("~p accepted with new stoplist ~p !~n",[self(),NewStoplist]),
%  {Now, NewStoplist, N};

%liftProcess({reject}, {Now, Stoplist, N}) ->
%  io:format("~p rejected with old stoplist ~p !~n",[self(),Stoplist]),
%  {Now, Stoplist, N};

% assistant function to get the given N FloorAgent from the full list for button light purpose.
getFloorAgent(Floor, FloorAgentList) ->
  lists:nth(Floor, FloorAgentList).

% the failed attempt to lock Lift agents after sending propose messages.
lockedLiftProcess({Now, Stoplist, N, _FloorAgentList}) ->
  receive
    {accept, Floor, _FloorAgent, Dir} ->
      NewStoplist = insert(Stoplist, Now, Dir, Floor),
      %io:format("~p accepted with new stoplist ~p !~n",[self(),NewStoplist]),
      {Now, NewStoplist, N, _FloorAgentList};
    {reject} ->
      %io:format("~p rejected with old stoplist ~p !~n",[self(),Stoplist]),
      {Now, Stoplist, N, _FloorAgentList}
  end.

% assistant function to get the FloorAgentList.
liftProcess({getFloorAgentList, FloorAgentList}, {_Now, _Stoplist, _N, _FloorAgentList}) ->
  %io:format("~p lift gets FloorAgentList:~p ~n", [self(), FloorAgentList]),
  {_Now, _Stoplist, _N, FloorAgentList};

% function branch to handle request message from a given Floor agent. tell it your estimate of 
% wait time.
liftProcess({request, Floor, FloorAgent, Dir}, {Now, Stoplist, N, _FloorAgentList}) ->

  %io:format("get request from flooragent: ~p with direction ~p~n",[FloorAgent,Dir]),
  FloorAgent ! {propose, Dir, self(), waitTime(Stoplist, Now, Dir, Floor)},
  lockedLiftProcess({Now, Stoplist, N, _FloorAgentList});
%  {Now, Stoplist};

% stop messages are received from the buttons inside the lift

% for our simulation they will be sent directly from the command line to the lift process

liftProcess({stop, Floor}, {Now, Stoplist, N, _FloorAgentList}) ->
  %io:format("We need to stop at ~p~n",[Floor]),
  {Now, stopAt(Stoplist, Now, Floor), N, _FloorAgentList}
;


% arrived messages come from the Cabin agent that runs the lift cabin up and 

% down in its shaft. The lift agent should respond with an up, down, stop or wait message

% depending on what the lift needs to do next. Send stop if the lift should stop on this floor,

% send wait if the lift has nothing to do.

% in a word, a complicated, inefficient but workable logic to control stop, wait, up and down
% cases for Lift agents.
liftProcess({Cabin, arrived, Floor}, {_Now, Stoplist, N, FloorAgentList}) -> 

  %io:format("Message from Cabin with Floor and stoplist: ~p ~p ~n", [Floor,Stoplist]),   
  case {Floor, Stoplist, N} of

    {Floor, [], _N} ->
      Cabin ! {self(), wait}, 
      %io:format("just wait"), 
      {Floor, [], _N, FloorAgentList};	
    {Floor, [Floor|[]], _N} ->
      Cabin ! {self(), stop},
      io:format("just stop~n"),
      getFloorAgent(Floor, FloorAgentList) ! {turnoff, stay},
      {Floor, [], _N, FloorAgentList};
    {Floor, [Floor|Rest], _N} when (Floor < hd(Rest)) -> 
      Cabin ! {self(), stop}, 
      io:format("stop and up~n"),
      getFloorAgent(Floor, FloorAgentList) ! {turnoff, up}, 
      {Floor, Rest, _N, FloorAgentList};

    {Floor, [Floor|_Rest], _N} -> 
      Cabin ! {self(), stop}, 
      io:format("stop and down~n"), 
      getFloorAgent(Floor, FloorAgentList) ! {turnoff, down},
      {Floor, _Rest, _N, FloorAgentList};

    {Floor, [H|Rest], _N} when Floor < H -> 
      Cabin ! {self(), up}, 
      io:format("nonstop and up~n"), 
      {Floor, [H|Rest], _N, FloorAgentList};
    {Floor, _Rest, N} when Floor == N -> 
      Cabin ! {self(), down}, 
      io:format("top and down~n"), 
      getFloorAgent(Floor, FloorAgentList) ! {turnoff, down}, 
      {Floor, _Rest, N, FloorAgentList};
    
    {Floor, [H|Rest], _N} when Floor > H -> 
      Cabin ! {self(), down}, 
      io:format("nonstop and down~n"), 
      {Floor, [H|Rest], _N, FloorAgentList}
  end
.



% Create a new lift agent with an empty stop list and located on Floor 1.

% Takes lift number as parameter but not used.

newLift(N) ->

  agent:newAgent(fun liftProcess/2,  {1, [], N, []}).
