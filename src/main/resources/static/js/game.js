$( document ).ready(function() {

    connectToSocket();


    $("#form-chat").submit(function (event) {
        event.preventDefault();

        console.log("form-chat click");

        let msg = document.getElementById("form-chat").msg.value;
        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "chat",
                "message": msg
            }));
    });

    /*$("#btn-chat-message").click(function () {
        //alert("btn-chat-message");

        //writeMessage();
        let msg = document.getElementById("form-chat").msg.value;

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "chat",
                "message": msg
            }));
    });*/
});

function writeMessage(message){
    //let form = document.getElementById("form-chat");
    let now = new Date().toLocaleString()
    document.getElementById("chat").innerHTML = document.getElementById("chat").innerHTML + now  + " | " + message.sender + ": " + message.message + "<br />";

    /*$.ajax({
        url: url + "/game/gameplay",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({
            "message": message,
            "gameUUID": gameUUID,
            "userLogin": currentUser
        }),
        success: function (data) {
            alert("message sent to controller. Answer: " + data)
        },
        error: function (error) {
            console.log(error);
        }
    })*/

}