% Top level module
-module(cchat).
-export([server/0,client/0,start/0,start2/0, send_job/3]).
-include_lib("./defs.hrl").

%% Start a server
server() ->
    Server = "shire",
    genserver:start(list_to_atom(Server), server:initial_state(Server), fun server:handle/2).

%% Start a client GUI
client() ->
    gui:start().

%% Start local server and one client
start() ->
    server(),
    client().

%% Start local server and two clients
start2() ->
    server(),
    client(),
    client().

send_job(Server, Func, Inputs) ->
	Users = genserver:request(list_to_atom(Server), {getUsers}),
	Pids = selectPids(Users),
	Tasks = makeTaskTuple(Func, Inputs),
	Users&Tasks = assignTasks(Pids, Tasks),
	Clients&Refs = [{Client, make_ref(), Task}|| {Client, Task} <- Users&Tasks],
	Refs = [Ref || {_, Ref, _} <- Clients&Refs],
	lists:foreach(fun send_job2/1, Clients&Refs),
	handleRefs([], Refs).

send_job2({Client, Ref, Task}) ->
	MySelf = self(), 
	spawn(fun() -> MySelf!{Ref, genserver:request(Task)}end).
	%genserver:request(Client, Task, infinity).

handleRefs(Values, []) ->
	Values;
handleRefs(Values, [Href | TRefs]) ->
	receive
		{HRef, Response} ->
				handleRefs([Values | Response], Trefs)
	end.

selectPids([]) ->
	[];
selectPids([{_, Pid}|T]) ->
	[Pid | selectPids(T)].

makeTaskTuple(_, []) ->
	[];
makeTaskTuple(F, [H|T]) ->
	[{executeFunc, F, H} | makeTaskTuple(F, T)].

assign_tasks([], _) -> [] ;
assign_tasks(Users, Tasks) ->
  [  {lists:nth(((N-1) rem length(Users)) + 1, Users), Task}
  || {N,Task} <- lists:zip(lists:seq(1,length(Tasks)), Tasks) ].
