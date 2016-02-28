-module(channel).
-export([handle/2, initial_state/1]).
-include_lib("./defs.hrl").

%% Produce initial state
initial_state(ChannelName) ->
    #channel_st{name = list_to_atom(ChannelName)}.


handle(St, {join, User}) ->
	NewState = St#channel_st{users = [User | St#channel_st.users]},
	{reply, ok, NewState};

handle(St, {leave, User}) ->
	NewState = St#channel_st{users = lists:delete(User, St#channel_st.users)},
	{reply, ok, NewState};

% Sending messages
handle(St, {msg, Msg}) ->

	%ChannelAtom = list_to_atom(Channel),
	%genserver:request(St#client_st.server, {msg, user(St), Msg}),
	sendToUsers(St#channel_st.users, Msg),
    {reply, ok, St}.
    %{reply, {error, not_implemented, "Not implemented"}, St} ;



send(Receiver, Msg) ->
	genserver:request(Receiver, Msg).

sendToUsers([], _) ->
  ok;

sendToUsers([ H | T ], Msg) ->
  spawn(fun() -> send(H, Msg) end),
  sendToUsers(T, Msg).

