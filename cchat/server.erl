-module(server).
-export([handle/2, initial_state/1]).
-include_lib("./defs.hrl").

%% inititial_state/2 and handle/2 are used togetger with the genserver module,
%% explained in the lecture about Generic server.

% Produce initial state
initial_state(ServerName) ->
    #server_st{server = list_to_atom(ServerName)}.

%% ---------------------------------------------------------------------------

%% handle/2 handles requests from clients

%% All requests are processed by handle/2 receiving the request data (and the
%% current state), performing the needed actions, and returning a tuple
%% {reply, Reply, NewState}, where Reply is the reply to be sent to the client
%% and NewState is the new state of the server.

%% Connect to server
handle(St, {connect, Nick}) ->
	case lists:member(Nick, St#server_st.users) of
		false ->
			NewState = St#server_st{users = [Nick | St#server_st.users]},		
			{reply, ok, NewState};
			%{reply, {error, not_implemented, "Not implemented"}, St} ;
		_ ->
			{reply, {error, user_already_connected, "Not implemented"}, St}
		end;


% Join channel
handle(St, {join, Channel, UserPid}) ->
	case lists:member(Channel, St#server_st.channels) of
		false ->
			ChannelPid = genserver:start(list_to_atom(Channel), channel:initial_state(Channel), fun channel:handle/2),
			%io:fprint("ChannelPid = ~p~n", ChannelPid),
			Response = genserver:request(ChannelPid, {join, UserPid}),
			case Response of
				ok ->
					NewState = St#server_st{channels = [Channel | St#server_st.channels]},
					{reply, ok, NewState};
				_ ->
					Response
			end;
		_ ->
			Response = genserver:request(list_to_atom(Channel), {join, UserPid}),
			{reply, ok, St}
			
	end;
    %{reply, {error, not_implemented, "Not implemented"}, St} ;

handle(St, {leave, Channel, User}) ->
	Response = genserver:request(list_to_atom(Channel), {leave, User}),
	case Response of
				ok ->
					NewState = St#server_st{channels = lists:delete(Channel, St#server_st.channels)},
					{reply, ok, NewState};
				_ ->
					Response
	end;
	

handle(St, Request) ->
    io:fwrite("Server received: ~p~n", [Request]),
    Response = "hi!",
    io:fwrite("Server is sending: ~p~n", [Response]),
    {reply, Response, St}.

	
