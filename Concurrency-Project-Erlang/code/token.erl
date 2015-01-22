-module(token).
-export([issueToken/0]).

% function to receive token request messages from Floor agents.
issueToken() ->
  receive
    {ask, FloorAgent, Dir} ->
      io:format("Token issued to ~p with direction ~p ~n", [FloorAgent, Dir]),
      FloorAgent ! {token, Dir}, %issue the token and turn to wait loop.
      waitRelease()
  end.

% function to wait for token released fron a specific Floor agent with the token in hand.
waitRelease() ->
  receive
    {release, FloorAgent} ->
      io:format("Token released from ~p ~n", [FloorAgent]),
      issueToken()
  end.