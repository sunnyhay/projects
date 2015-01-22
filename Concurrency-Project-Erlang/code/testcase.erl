-module(testcase).
-export([test/0]).
%-import(waitTime,[waitTime/4]).  %modify to your own waitTime function;
%-import(insert,[insert/4]).      %modify to your own insert function.

test() ->
  % definition of the huge test list below.  
  % the element in Testcase is {Stoplist, Now, Dir, Floor} where Dir is direction
  % and Floor is current floor number to be inserted into Stoplist. Now is current
  % stopping floor.

  Testcase = [{[2,4,6,8,4], 1, up, 2}         %test case 1 for up
             ,{[2,4,6,8,4], 1, up, 3}         %test case 2 for up
             ,{[2,4,6,8,4], 1, up, 5}         %test case 3 for up
             ,{[2,4,6,8,6,4], 1, down, 7}     %test case 1 for down
             ,{[2,4,6,8,4], 1, down, 6}       %test case 2 for down
             ,{[2,4,6,8,6], 1, down, 4}       %test case 3 for down

             ,{[2,4,6,8,4], 9, up, 1}         %test case 4 for up
             ,{[2,4,6,8,4], 9, up, 2}         %test case 5 for up
             ,{[2,4,6,8,4], 9, up, 5}         %test case 6 for up
             ,{[2,4,6,8,4], 9, down, 1}       %test case 4 for down
             ,{[2,4,6,8,6,4], 9, down, 7}     %test case 5 for down
             ,{[2,4,6,8,4], 9, down, 6}       %test case 6 for down
             ,{[2,4,6,8,6], 5, down, 7}       %test case 7 for down
             ,{[5,3,6,7,2], 9, down, 5}       %test case 8 for down
             ,{[4,5,7,8,9], 2, down, 9}       %test case 9 for down
             ,{[2,4,6,8,4], 1, down, 2}       %test case 7 for up
             ,{[2,1,6,8,4], 9, up, 2}]        %test case 8 for up
             ,

 testInsert(Testcase)
 ,testWaittime(Testcase)
.

testInsert([]) -> [];

testInsert([H|T]) ->
  io:format("Insert Result is: ~p ~n!", [insertTest(H)]),
  testInsert(T)
.

testWaittime([]) -> [];

testWaittime([H|T]) ->
  io:format("Waittime Result is: ~p ~n!", [waittimeTest(H)]),
  testWaittime(T)
.


%modify to your own insert function.
insertTest({Stoplist, Now, Dir, Floor}) ->
  insert:insert(Stoplist, Now, Dir, Floor)
.

%modify to your own waitTime function;
waittimeTest({Stoplist, Now, Dir, Floor}) ->
  waitTime:waitTime(Stoplist, Now, Dir, Floor)
. 


