% This record defines the structure of the client process.
% Add whatever other fields you need.
% It contains the following fields:
%   gui: the name (or Pid) of the GUI process.
%   nick: name of the user
%   server: either disconnected or serverAtom for the server its connected to.
-record(client_st, {gui, nick, server = disconnected, channels = []}).

% This record defines the structure of the server process.
% Add whatever other fields you need.
-record(server_st, {server, channels = [], users =[]}).

% This record defines the structure of the channel process.
% It contains the following fields:
-record(channel_st, {name, users = []}).
