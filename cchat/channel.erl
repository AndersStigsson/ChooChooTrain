-module(channel).
-export([handle/2, initial_state/1]).
-include_lib("./defs.hrl").

%% Produce initial state
initial_state(ChannelName) ->
    #channel_st{name = ChannelName}.

% Joining a the channel.
handle(St, {join, UserPid}) ->
	NewState = St#channel_st{users = [UserPid | St#channel_st.users]},
	{reply, ok, NewState};

% Leaving this channel.
handle(St, {leave, User}) ->
	NewState = St#channel_st{users = lists:delete(User, St#channel_st.users)},
	{reply, ok, NewState};

% Sending messages
handle(St, {msg, Msg, {Sender, User}}) ->  %user@{senderNick, clientPid}
	
	case lists:member(User, St#channel_st.users) of
		true ->
			SendToAllOfThese = lists:delete(User, St#channel_st.users),
			sendToUsers(St, SendToAllOfThese, Sender, Msg),
    		{reply, ok, St}.
    	_ -> 
   			{reply, {error, user_not_joined, "Not in this channel."}, St} ;


% The actual sending of the message via the server.
send(St, Receiver, Sender, Msg) ->
	IncomingAtom = {incoming_msg, St#channel_st.name, Sender, Msg},
	genserver:request(Receiver, IncomingAtom).

% Recursive function to send to every member of this channel (client).
sendToUsers(_, [], _, _) ->
  ok;

sendToUsers(St, [ Head | Tail ], Sender, Msg) ->
  spawn(fun() -> send(St, H, Sender, Msg) end),
  sendToUsers(St, T, Sender, Msg).

