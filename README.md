# CommandNexus
Simple utility for serializing and deserialising commands to JSON for sending between network clients.

## Setup
The CommandNexus requires a client (or server) type that the commands will be sent through. This could be a webapp client, a socket client or other.
For the sake of this guide, we will just use a plain old class:
```java
class MyClient {

    public void sendMessage(String message) {
        //Send message to the client
    }

}
```
The clients should call the nexus' onCommand method whenever they receive a command to be handled.

The CommandNexus itself can now be instantiated using this client class:
```java
CommandNexus<MyClient> nexus = new CommandNexus<>(MyClient::sendMessage);
```

## Handling commands
To handle a command we need a class to represent the command. Say we have a registry for people. Each person has an ID to uniquely identify them, a name and an age.
```java
class PersonInfoCommand extends Command {

    private int id;
    private String name;
    private int age;

    public PersonInfoCommand() {
        super("person_info"); //Command name
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

}
```
Now we need to tell the nexus we want to handle all incoming commands with the name "person_info":
```java
        nexus.handleCommand("person_info", PersonInfoCommand.class, (sender, command) -> {
            //Sender is of type MyClient
            //Command is of type PersonInfoCommand
            //We can access its getters:
            System.out.println(command.getId());
            System.out.println(command.getName());
            System.out.println(command.getAge());
        });
```
If the nexus' onCommand was called with the following JSON input:
```json
{
  "commandname": "person_info",
  "id": 42,
  "name": "John Smith",
  "age": 26
}
```
then the generated PersonInfoCommand object will have the corresponding values.

## Sending a command
To send a command, we need to define a class representing the command to send. Say we wanted to request info about a person from one of the clients by ID.
```java
class RequestPersonInfoCommand extends Command {

    private int id;

    public RequestPersonInfoCommand(int id) {
        super("person_info_request"); //Command name
        this.id = id;
    }

}
```

To send this command to a client it needs to be serialized (into JSON). The CommandNexus can help us with this:
```java
        MyClient client = <some client>
        try {
            nexus.sendCommand(client, new RequestPersonInfoCommand(2));
        } catch (IOException e) { //Any IOException thrown by the client when sending the command can be handled here
            e.printStackTrace();
        }
```
This will generate the following JSON and send it to the client:
```json
{
  "commandname": "person_info_request",
  "id": 2
}
```
