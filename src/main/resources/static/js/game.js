$( document ).ready(function() {

    if (gameStatus === "NEW" || gameStatus === "IN_PROGRESS") {
        connectToSocket();
    }

    console.log(chat);
    document.getElementById("chat-box").scrollTop = document.getElementById("chat-box").scrollHeight;

    setTimeout(downloadCanvas(), 1000);

    if (gameStatus === "IN_PROGRESS") {
        beginTheGame();
    }

    $("#form-chat").submit(function (event) {
        event.preventDefault();

        let msg = document.getElementById("form-chat-input").value;
        if (msg) {
            stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
                {
                    "type": "chat",
                    "message": msg
                }));
        }

        document.getElementById("form-chat").reset();
    });

    $("#btn-canvas-clear").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "clear canvas"
            }));

    });

    $("#btn-start-game").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "begin game"
            }));

    });

    $("#btn-game-link").click(function (event) {
        event.preventDefault();

        const el = document.createElement('textarea');
        el.value = gameUUID;
        el.setAttribute('readonly', '');
        el.style.position = 'absolute';
        el.style.left = '-9999px';
        document.body.appendChild(el);
        el.select();
        document.execCommand('copy');
        document.body.removeChild(el);
    });

    $("#link-game-cancel").click(function (event) {
        event.preventDefault();

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "cancel game"
            }));
    });

    $("#link-game-leave").click(function (event) {

        stompClient.send("/app/game-socket/" + gameUUID, {}, JSON.stringify(
            {
                "type": "command",
                "command": "leave game"
            }));
        stompClient.disconnect();

        $("#link-game-leave").unbind();

        setTimeout(function() {
            window.location.href = "/";
        }, 2000);

        return false;
    });

});






