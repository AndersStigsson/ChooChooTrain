-module(client).
-export([handle/2, initial_state/2]).
-include_lib("./defs.hrl").

%% inititial_state/2 and handle/2 are used togetger with the genserver module,
%% explained in the lecture about Generic server.

%% Produce initial state
initial_state(Nick, GUIName) ->
    #client_st {nick = Nick, gui = GUIName }.

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
    Response = genserver:request(ServerAtom, {connect, user(NewState)}),
    {reply, ok, NewState} ;
    %{reply, {error, not_implemented, "Not implemented"}, St} ;

%% Disconnect from server
handle(St, disconnect) ->
	case St#client_st.server of
		disconnected ->
		    {reply, {error, user_not_connected, "You are not connected"}, St};

		_ ->
			NewState = St#client_st{server = disconnected},
			Response = genserver:request(St#client_st.server, {disconnect, user(St)}),
     		{reply, ok, NewState}
	end;

% Join channel
handle(St, {join, Channel}) ->
    % {reply, ok, St} ;
    {reply, {error, not_implemented, "Not implemented"}, St} ;

%% Leave channel
handle(St, {leave, Channel}) ->
    % {reply, ok, St} ;
    {reply, {error, not_implemented, "Not implemented"}, St} ;

% Sending messages
handle(St, {msg_from_GUI, Channel, Msg}) ->
    % {reply, ok, St} ;
    {reply, {error, not_implemented, "Not implemented"}, St} ;

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



%% Incoming message
handle(St = #client_st { gui = GUIName }, {incoming_msg, Channel, Name, Msg}) ->
    gen_server:call(list_to_atom(GUIName), {msg_to_GUI, Channel, Name++"> "++Msg}),
    {reply, ok, St}.

user(St) ->
  {St#client_st.nick, self()}.
