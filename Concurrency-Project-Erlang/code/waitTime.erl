% This function aims to calculation of wait time before a request can be served.
% It's based on a new stoplist returned by insert/4 function for a lift controller.
% Four arguments: Stoplist, Now, Dir, Floor. Stoplist contains a list of all floors to
% be stopped; Now is the current floor; Dir either up or down; Floor represents the 
% stop request of a floor on which a button is pressed.
% Directly invoke insert/4 function and get the new stoplist. Then calculate the time.

-module(waitTime).
-export([waitTime/4]).

waitTime(Stoplist, Now, Dir, Floor) ->
  %io:format("Each's original stoplist:~p ~n",[Stoplist]),
  NewStoplist = insertPart(Stoplist, Now, Dir, Floor),
  %io:format("Print each's new Stoplist: ~p with floor ~p ~n",[NewStoplist,Floor]),
  calWaitTime(NewStoplist, Now, Floor, 0).

calWaitTime([Floor|[]], Now, Floor, Waittime) ->
  (Waittime + abs(Floor, Now));

calWaitTime([H|Rest], Now, Floor, Waittime) ->
  %io:format("now waittime:~p~n", [Waittime]),
  calWaitTime(Rest, H, Floor, Waittime + abs(H, Now) + 5).

abs(A, B) ->
  case ((A - B) > 0) of
    true -> (A - B);
    false -> (B - A)
  end.

% the basic logic to evaluate wait time is same as insert. The only difference is that
% the insertPart function returns part of the new Stoplist: a new list of all elements
% before current Floor and itself (including redundant).
insertPart([], _Now, _Dir, Floor) ->
  [Floor];
  
insertPart(Stoplist, Now, Dir, Floor) ->
  case Dir of
    up -> 
      case (Now < Floor) and (Floor < hd(Stoplist)) of
	true -> [Floor];
	false -> 
          case (length(Stoplist) =:= 1) of
            true ->
              case (Floor /= hd(Stoplist)) of
                true -> Stoplist ++ [Floor];
                false -> Stoplist
              end;
            false ->
              case ((hd(Stoplist) < lists:nth(2,Stoplist)) and (Floor =:= hd(Stoplist))) of
                true -> [Floor];
                false -> upInsertPart(Stoplist, Floor)
              end
           end
      end;
    down -> 
      case (Now > Floor) and (Floor > hd(Stoplist)) of
	true -> [Floor];
	false -> 
          case (length(Stoplist) =:= 1) of
            true ->
              case (Floor /= hd(Stoplist)) of
                true -> Stoplist ++ [Floor];
                false -> Stoplist
              end;
            false ->
              case ((hd(Stoplist) > lists:nth(2,Stoplist)) and (Floor =:= hd(Stoplist))) of
                true -> [Floor];
                false -> downInsertPart(Stoplist, Floor)
              end
          end
      end
  end.

upInsertPart(Stoplist, Floor) ->
  upPart([], Stoplist, Floor).

downInsertPart(Stoplist, Floor) ->
  downPart([], Stoplist, Floor).

upPart(Stoplist, [], Floor) ->
  Stoplist ++ [Floor];

upPart(Before, [A|[B|_Rest]], Floor) when (A < Floor) and (Floor < B) ->
  Before ++ [A,Floor];

upPart(Before, [A|Rest], Floor) ->
  upPart(Before ++ [A], Rest, Floor).

downPart(Stoplist, [], Floor) ->
  Stoplist ++ [Floor];

downPart(Before, [A|[B|_Rest]], Floor) when (A > Floor) and (Floor > B) ->
  Before ++ [A,Floor];

downPart(Before, [A|Rest], Floor) ->
  downPart(Before ++ [A], Rest, Floor).