-module(channel).
-export([handle/2, initial_state/1]).
-include_lib("./defs.hrl").

%% Produce initial state
initial_state(ChannelName) ->
    #channel_st{name = list_to_atom(ChannelName)}.

% Joining a the channel.
handle(St, {join, User}) ->
	NewState = St#channel_st{users = [User | St#channel_st.users]},
	{reply, ok, NewState};

% Leaving this channel.
handle(St, {leave, User}) ->
	NewState = St#channel_st{users = lists:delete(User, St#channel_st.users)},
	{reply, ok, NewState};

% Sending messages
handle(St, {msg, Msg, {sender, user}}) ->  %user@{senderNick, clientPid}

	%ChannelAtom = list_to_atom(Channel),
	%genserver:request(St#client_st.server, {msg, user(St), Msg}),

	
	SendToAllOfThese = lists:delete(user, St#channel_st.users),
	NewState = St#channel_st{users = SendToAllOfThese},
	sendToUsers(St, SendToAllOfThese, sender, Msg),
    {reply, ok, St}.
    %{reply, {error, not_implemented, "Not implemented"}, St} ;


% The actual sending of the message via the server.
send(St, Receiver, Sender, Msg) ->
	%% Incoming message in client wants --> (someState, {incoming_msg, Channel, Name, Msg}
	IncomingAtom = {incoming_msg, St#channel_st.name, Sender, Msg},
	IncomingAtom2 = {incoming_msg, self(), Sender, Msg},
	genserver:request(Receiver, IncomingAtom).
	
	%genserver:request(Receiver, Msg).

% Recursive function to send to every member of this channel (client).
sendToUsers(_ ,[], _, _) ->
  ok;

sendToUsers(St, [ H | T ], Sender, Msg) ->
  spawn(fun() -> send(St, H, Sender, Msg) end),
  sendToUsers(St, T, Sender, Msg).

