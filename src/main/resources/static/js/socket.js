const url = 'http://localhost:8080';
let stompClient;
let playerRole;


function connectToSocket() {
    console.log("establishing socket connection with game: " + gameUUID);
    let socket = new SockJS(/*url + */"/game-socket");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log("connected to the frame: " + frame);
        stompClient.subscribe("/topic/game-progress/" + gameUUID, function (response) {
            alert("Stomp client got a message!");
            console.log("Stomp client got a message!");
            let data = JSON.parse(response.body);
            console.log(data);
        });
        }
    );
}