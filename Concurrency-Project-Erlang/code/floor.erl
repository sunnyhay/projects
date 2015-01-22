-module(floor).

-export([newFloor/3]).



newFloor(N, Lifts, TokenPid) -> agent:newAgent(fun floor/2, {N, Lifts, [], TokenPid}).

% function branch to deal with button light off case.
floor({turnoff, Dir}, {N, _Lifts, _ProposedList, _TokenPid}=State) ->
  io:format("~p-button light on floor ~p is off now!~n", [Dir,N]),
  State;


% function branch to deal with token if current Floor agent gets it.
% Right after sending request (wait time estimate) message, tells the TokenPid to release the token.
floor({token, Dir}, {N, Lifts, _ProposedList, TokenPid}=State) ->
  %io:format("Get token!"),
  [Lift ! {request, N, self(), Dir} || Lift <- Lifts],
  TokenPid ! {release, self()},
  State;

% function branch to deal with button up request from outside.
floor(up, {N, _Lifts, _ProposedList, TokenPid}=State) -> 
	
  %io:format("Receive up command on #~p floor with lifts:~p~n", [N,Lifts]),
  TokenPid ! {ask, self(), up},
  io:format("Up-button light on floor ~p is now on!~n", [N]),
  State
;

% function branch to deal with button down request from outside.


floor(down, {N, _Lifts, _ProposedList, TokenPid}=State) ->

  io:format("Down-button light on floor ~p is now on!~n", [N]),
  TokenPid ! {ask, self(), down},
  State
;

% function branch to deal with propose messages from all the Lift agents.
floor({propose, Dir, LiftAgent, Waittime}, {N, Lifts, ProposedList, _TokenPid}) ->
  %io:format("get propose from Lift: ~p ~n",[LiftAgent]),
  case ((length(ProposedList) +1) =:= length(Lifts)) of
    % the last message arrives.
    true ->
      %io:format("ProposedList: ~p~n", [ProposedList]),
      selectBest(ProposedList ++ [{LiftAgent, Waittime}], {N, Dir}, {self(), 100}),
      %TokenPid ! {release, self()},
      {N, Lifts, [], _TokenPid};
    % still missing messages from some Lift agents.
    false ->
      %io:format("length of ProposedList: ~p~n", [length(ProposedList)]),
      {N, Lifts, ProposedList ++ [{LiftAgent, Waittime}], _TokenPid}      
  end.


selectBest([], {N, Dir}, {Lift, _Time}=Best) -> 
  %io:format("Last Best is: ~p~n",[Best]),
  Lift ! {accept, N, self(), Dir}, Best;


selectBest([{Lift, Time}|Rest], {_N, _Dir}, {BLift, BTime}) when Time < BTime -> 
  case BLift =:= self() of
    true ->
      selectBest(Rest, {_N, _Dir}, {Lift, Time});
    false ->
      %io:format("Current Best< is: ~p~n",[Best]),
      BLift! {reject}, selectBest(Rest, {_N, _Dir}, {Lift, Time})
  end;


selectBest([{Lift, _Time}|Rest], {_N, _Dir}, Best) -> 
  %io:format("Current Best>= is: ~p~n",[Best]),
  Lift ! {reject}, selectBest(Rest, {_N, _Dir}, Best).
