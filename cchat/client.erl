-module(client).
-export([handle/2, initial_state/2]).
-include_lib("./defs.hrl").

%% inititial_state/2 and handle/2 are used togetger with the genserver module,
%% explained in the lecture about Generic server.

%% Produce initial state
initial_state(Nick, GUIName) ->
    #client_st{nick = Nick, gui = GUIName}.

%% ---------------------------------------------------------------------------

%% handle/2 handles each kind of request from GUI

%% All requests are processed by handle/2 receiving the request data (and the
%% current state), performing the needed actions, and returning a tuple
%% {reply, Reply, NewState}, where Reply is the reply to be sent to the
%% requesting process and NewState is the new state of the client.

%% Connect to server
handle(St, {connect, Server}) ->
	ServerAtom = list_to_atom(Server),
	NewState = St#client_st{server = ServerAtom	}, 
	
	case catch genserver:request(ServerAtom, {connect, user(NewState)}) of
		ok ->
			{reply, ok, NewState};
		{EXIT, _} -> {reply, {error, server_not_reached, "Server unreachable"}, St};
		{error, nick_taken, _} ->
			{reply,{error, nick_taken, "Nick already taken"}, St}
	end;
	%{reply, {error, not_implemented, "Not implemented"}, St} ;
	

%% Disconnect from server
handle(St, disconnect) ->
	case checkServers(St#client_st.channels) of 
		true ->
			case St#client_st.server of
				disconnected ->
					{reply, {error, user_not_connected, "You are not connected"}, St};

				_ ->
					NewState = St#client_st{server = disconnected},
					Response = genserver:request(St#client_st.server, {disconnect, user(St)}),
			 		{reply, ok, NewState}
			end;
		_ ->
			{reply, {error, leave_channels_first, "Leave all channels before disconnecting"}, St}
	end;

% Join channel
handle(St, {join, Channel}) ->
	case St#client_st.server of
		disconnected ->
			{reply, {error, user_not_connected, "Connect before joining a chatroom"}, St};
		_ ->
			case lists:member(Channel, St#client_st.channels) of
				false ->
					Response = genserver:request(St#client_st.server, {join, Channel, self()}),
					case Response of
						ok ->
							NewState = St#client_st{channels = [Channel | St#client_st.channels]},
							{reply, ok, NewState};
						_ ->
							Response
					end;
				_ ->
					{reply, {error, user_already_joined, "Already in this chatroom"}, St}
			end
	end;
    %{reply, {error, not_implemented, "Not implemented"}, St} ;

%% Leave channel
handle(St, {leave, Channel}) ->
	case St#client_st.server of
		disconnected ->
			{reply, {error, user_not_connected, "Not connected to a server"}, St};
		
		_ ->
			case lists:member(Channel, St#client_st.channels) of
				true ->
					Response = genserver:request(St#client_st.server, {leave, Channel, self()}),
						case Response of
							ok ->
								NewState = St#client_st{channels = lists:delete(Channel, St#client_st.channels)},
								{reply,ok, NewState};
							_ ->
								Response
						end;
				_ ->
					{reply, {error, user_not_joined, "Already in this chatroom"}, St}
			end
	end;
	
    % {reply, ok, St} ;
    %{reply, {error, not_implemented, "Not implemented"}, St} ;

% Sending messages
handle(St, {msg_from_GUI, Channel, Msg}) ->
	case lists:member(Channel, St#client_st.channels) of
		true ->
			%ChannelAtom = list_to_atom(Channel),
			genserver:request(list_to_atom(Channel), {msg, Msg, user(St)}), % sends itself as user to be able to remove from the recievers of the msg.
			{reply, ok, St} ;
			%{reply, {error, not_implemented, "Not implemented"}, St} ;
		_ ->
			{reply, {error, user_not_joined, "Not in this channel"}, St}
	end;
%% Get current nick
handle(St, whoami) ->
    {reply, St#client_st.nick, St} ;

%% Change nick
handle(St, {nick, Nick}) ->
	case St#client_st.server of 
		disconnected ->
			NewState = St#client_st{nick = Nick},	 
    		{reply, ok, NewState} ;
		_ ->	
    		{reply, {error, user_already_connected, "Can't change nick when connected"}, St}
	end;


%% Task execution.
handle(St, {executeFunc, F, I}) ->
	Response = F(I),
	{reply, Response, St};

%% Incoming message
handle(St = #client_st { gui = GUIName }, {incoming_msg, Channel, Name, Msg}) ->
    gen_server:call(list_to_atom(GUIName), {msg_to_GUI, Channel, Name++"> "++Msg}),
    {reply, ok, St}.

user(St) ->
  {St#client_st.nick, self()}.

checkServers([]) ->
	true;
checkServers(_) ->
	false.
