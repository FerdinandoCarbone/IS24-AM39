## Codex Naturalis developed by Team AM39
### How to Launch program:
#### To start a server instance proceed by executing via cli:
##### java -jar CodexNaturalis.jar --server \<Server Port>
Server instances are hosted by default on 8081 but port can be specified. For RMI port is always 1099
#### To start a client instance execute via cli:
##### java -jar CodexNaturalis.jar --client \<Server Address> \<Server Port> \<Interface>
\<Interface> must be either "tui" or "gui". Default \<Server Port> is 8081, but can be adjusted, \<Server Address> is required
### Before launching:
Please make a folder named "savedata" where your jar file is stored else Client will crash ONLY at first boot