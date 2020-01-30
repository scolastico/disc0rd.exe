function sendMessage() {
    if (document.getElementById("title").value === "" || document.getElementById("message").value === "") {
        swal.fire({
           icon: "error",
           title: "Oops...",
           text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttpActionsSend = new XMLHttpRequest();
    xmlHttpActionsSend.open( "POST", "../api/v1/admin/sendMessage", true );
    xmlHttpActionsSend.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpActionsSend.timeout = 60000;
    xmlHttpActionsSend.onreadystatechange = function() {
        if (xmlHttpActionsSend.readyState === 4 && xmlHttpActionsSend.status === 200) {
            if (JSON.parse(xmlHttpActionsSend.responseText).status === "ok") {
                const Toast = Swal.mixin({
                    toast: true,
                    position: 'top-end',
                    showConfirmButton: false,
                    timer: 3000,
                    timerProgressBar: true,
                    onOpen: (toast) => {
                        toast.addEventListener('mouseenter', Swal.stopTimer);
                        toast.addEventListener('mouseleave', Swal.resumeTimer);
                    }
                });
                Toast.fire({
                    icon: 'success',
                    title: 'Message Send!'
                });
            } else {
                window.location.replace("logout.html");
            }
        }
    };
    xmlHttpActionsSend.send( "title=" + encodeURI(document.getElementById("title").value) + "&message=" + encodeURI(document.getElementById("message").value) );
    document.getElementById("title").value = "";
    document.getElementById("message").value = "";
}