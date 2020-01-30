function sendSettings() {
    if (document.getElementById("w2gDefaultPlayback").value === "") {
        swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Please enter a value in all fields!"
        });
        return;
    }
    const xmlHttpSettingsSend = new XMLHttpRequest();
    xmlHttpSettingsSend.open( "POST", "../api/v1/admin/saveSettings", true );
    xmlHttpSettingsSend.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    xmlHttpSettingsSend.onreadystatechange = function() {
        if (xmlHttpSettingsSend.readyState === 4 && xmlHttpSettingsSend.status === 200) {
            if (JSON.parse(xmlHttpSettingsSend.responseText).status === "ok") {
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
                    title: 'Saved!'
                });
            } else {
                window.location.replace("logout.html");
            }
        }
    };
    xmlHttpSettingsSend.send( "w2gDefaultPlayback=" + encodeURI(document.getElementById("w2gDefaultPlayback").value) );
}

const xmlHttpSettings = new XMLHttpRequest();
xmlHttpSettings.open( "GET", "../api/v1/admin/getSettings", true );
xmlHttpSettings.onreadystatechange = function() {
    if (xmlHttpSettings.readyState === 4 && xmlHttpSettings.status === 200) {
        if (JSON.parse(xmlHttpSettings.responseText).status === "ok") {
            document.getElementById("w2gDefaultPlayback").value = JSON.parse(xmlHttpSettings.responseText).settings.w2gDefaultPlayback;
        } else {
            window.location.replace("logout.html");
        }
    }
};
xmlHttpSettings.send( "w2gDefaultPlayback=" + encodeURI(document.getElementById("w2gDefaultPlayback").value) );
