% This function returns a new stoplist when an internal button is pressed inside the lift.
% It's based on  insert/4 function for a lift controller.
% Three arguments: Stoplist, Now, Floor. Stoplist contains a list of all floors to
% be stopped; Now is the current floor;  Floor represents the 
% stop request of a floor on which a button is pressed.
% Indirectly invoke insert/4 function and get the new stoplist. 
% Define the direction: up if floor > now, down elsewhere.

% Original understanding to stopAt logic seems to be too simple and lazy compared to the right
% logic. But when I learnt from others about the corrent understanding to stopAt logic, it's too
% late for me to implement it. Sorry about this.

-module(stopAt). 
-export([stopAt/3]).
-import(insert,[insert/4]).

% the stopAt logic follows the insert: 
% if current lift runs upwards, using insert with direction up after comparing Now and Floor;
% vice versa for downward
stopAt([], _Now, Floor) ->
  [Floor];

stopAt(Stoplist, Now, Floor) ->
  case (Floor > Now) of
    true -> insert(Stoplist, Now, up, Floor);
    false -> insert(Stoplist, Now, down, Floor) 
  end.

