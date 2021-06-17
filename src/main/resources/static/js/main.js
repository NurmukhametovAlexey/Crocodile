const url = 'http://localhost:8080';

$( document ).ready(function() {
    alert("Hello, wazzup?");


    $("#btn-game").click(function () {
        alert("btn-game");

        $.ajax({
            type: 'POST',
            url:url + "/game/start",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify({
                "creatorLogin": "Bob",
                "difficulty": 2
            }),
            success: function (data) {
                console.log(data);
                let gameUUID = data.gameUUID;
                window.location.href = "/game?uuid="+gameUUID;
            },
            error: function (error) {
                console.log(error);
            }
        });
    });
});


