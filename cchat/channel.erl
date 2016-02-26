-module(channel).
-export([join/2, leave/2, initial_state/1]).
-include_lib("./defs.hrl").

%% Produce initial state
initial_state(ChannelName) ->
    #channel_st{name = list_to_atom(ChannelName)}.


%Join channel
join(St, {User, Channel}) ->
	{Nick, _} = User,
	case lists:keymember(Nick, 1, St#channel_st.users) of
		false ->
			NewState = St#channel_st{users = [User | St#channel_st.users]},
			Response = genserver:request(St#client_st.server, {join, Channel}),
			{reply, ok, NewState};
		_ ->
			{reply, {error, user_already_joined, "You are already a member of this chatroom"}, St}				
	end.		
		
%% Leave channel
leave(St, {User, Channel}) ->
	{Nick, _} = User,
	case lists:keymember(Nick, 1, St#channel_st.users) of
		true ->
			NewState = St#channel_st{users = lists:delete(User, St#channel_st.users)},
			Response = genserver:request(NewState#client_st.server, {leave, Channel}),
			{reply, ok, NewState};
		_ ->
			{reply, {error, user_not_joined, "You are not a member of that chatroom"}, St}
			
	end.
	
    %{reply, {error, not_implemented, "Not implemented"}, St} ;

% Sending messages
handle(St, {msg_from_GUI, Channel, Msg}) ->

	%ChannelAtom = list_to_atom(Channel),
	%genserver:request(St#client_st.server, {msg, user(St), Msg}),
    {reply, ok, St}.
    %{reply, {error, not_implemented, "Not implemented"}, St} ;




sendToUsers([], _) ->
  ok;

sendToUsers([ {_, Pid} | T ], Msg) ->
  genserver:request(Pid, Msg),
  sendToUsers(T, Msg).

