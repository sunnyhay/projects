% Construct a lift simulation scenario based on number of lifts and 

% number of floors.




-module(simulation).

-export([newScenario/2,test/0]).



newScenario(NumLifts, NumFloors) ->

    NewCabin = cabin:newGui(NumLifts, NumFloors), % NewCabin is a function

    % A token PID is created for token issuing to Floor agents.
    TokenPid = spawn(token, issueToken, []),
    Lifts = [lift:newLift(X) || X <- lists:seq(1, NumLifts)], %forCons(1, NumLifts, fun lift:newLift/1),

    Floors = [floor:newFloor(X, Lifts, TokenPid) || X <- lists:seq(1, NumFloors)], %forCons(1, NumFloors, fun (I) -> floor:newFloor(I, Lifts) end),

    % Each lift must keep a list of all Floor agents for purpose of button light. Send to them here.
    [Lift ! {getFloorAgentList, Floors} || Lift <- Lifts],
    % Each lift needs to be attached to a new cabin.

    lists:foreach(fun({Lift, I}) -> NewCabin(Lift, I) end, lists:zip(Lifts, lists:seq(1,NumLifts))),

    {Lifts, Floors}
.


test() ->

  S=newScenario(3, 5),


  {[L1, L2, L3], [F1, F2, F3, F4, F5]} = S,

  F3 ! up,
  F4 ! down,
  F2 ! up,
  L2 ! {stop, 4}.
  %L1 ! {stop, 2}.
  %F1 ! up.