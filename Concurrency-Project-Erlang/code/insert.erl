% This function aims to creation of a new stoplist for a lift controller.
% Four arguments: Stoplist, Now, Dir, Floor. Stoplist contains a list of all floors to
% be stopped; Now is the current floor; Dir either up or down; Floor represents the 
% stop request of a floor on which a button is pressed.

% the principle of insert function is to do the following branch comparisons:
% (1) Direction, either up or down;
% (2) Now < Floor < first element in Stoplist; (use up direction as example)
% (3) Only one element in the Stoplist
% (3.1) the element =?= current floor
% (3.2) running up and floor == first element or floor == the last element in the Stoplist
% the testcase.erl includes all the tested conditions.

-module(insert).
-export([insert/4]).

insert([], _Now, _Dir, Floor) ->
  [Floor];

insert(Stoplist, Now, Dir, Floor) ->
  case Dir of
    up -> 
      case (Now < Floor) and (Floor < hd(Stoplist)) of
	true -> [Floor|Stoplist];
	false -> 
          case (length(Stoplist) =:= 1) of
            true -> 
              case (Floor /= hd(Stoplist)) of
                true -> Stoplist ++ [Floor];
                false -> Stoplist
              end;
            false ->
              case ((hd(Stoplist) < lists:nth(2,Stoplist)) and (Floor =:= hd(Stoplist))) or (Floor =:= lists:last(Stoplist)) of
                true -> Stoplist;
                false -> upInsert(Stoplist, Floor)
              end
          end
      end;
    down -> 
      case (Now > Floor) and (Floor > hd(Stoplist)) of
	true -> [Floor|Stoplist];
	false -> 
          case (length(Stoplist) =:= 1) of
            true ->
              case (Floor /= hd(Stoplist)) of
                true -> Stoplist ++ [Floor];
                false -> Stoplist
              end;
            false ->
              case ((hd(Stoplist) > lists:nth(2,Stoplist)) and (Floor =:= hd(Stoplist))) or (Floor =:= lists:last(Stoplist)) of
                true -> Stoplist;
                false -> downInsert(Stoplist, Floor)
              end
          end
      end
  end.

% below are recursive functions assistant to above primary function.
upInsert(Stoplist, Floor) ->
  up([], Stoplist, Floor).

downInsert(Stoplist, Floor) ->
  down([], Stoplist, Floor).

up(Stoplist, [], Floor) ->
  Stoplist ++ [Floor];

up(Before, [A|[B|Rest]], Floor) when (A < Floor) and (Floor < B) ->
  Before ++ [A|[Floor|[B|Rest]]];

up(Before, [A|Rest], Floor) ->
  up(Before ++ [A], Rest, Floor).

down(Stoplist, [], Floor) ->
  Stoplist ++ [Floor];

down(Before, [A|[B|Rest]], Floor) when (A > Floor) and (Floor > B) ->
  Before ++ [A|[Floor|[B|Rest]]];

down(Before, [A|Rest], Floor) ->
  down(Before ++ [A], Rest, Floor).  