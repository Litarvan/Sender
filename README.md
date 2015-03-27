# Sender
An easy local file sender, I created it just for learning socket with Java, but i did wanna share it :)

## How to use it graphically ?
Just launch it, then click on the '+' to see your ID. Each computer have an unique ID based of his local IP. To send file to another computer, the computer must have sender launched. Just take files and drop it into Sender, then type the destination computer ID, and it will sends file !

## How to use it with command line ?
First add the bin/ directory to your PATH variable (go search on google if you don't know how to).

- Type sender -h/--help to display the help
- Type sender -s/--send <Files> to send file
- Type sender -r/--server to start the server that receive files