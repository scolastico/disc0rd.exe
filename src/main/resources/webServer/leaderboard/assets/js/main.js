const hash = window.location.hash;
if (hash) {
    const xhr = new XMLHttpRequest();
    xhr.open("GET", "../api/v1/leaderboard/get/" + hash.substring(1), true);
    xhr.onload = function (e) {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {

                const json = JSON.parse(xhr.responseText);

                if (json.status === "ok") {

                    const leaderboard = json.leaderboard;

                    const main = d3.select('table');

                    const users = main
                        .selectAll('tr.user')
                        .data(leaderboard)
                        .enter()
                        .append('tr')
                        .attr('class', 'user');

                    users
                        .append('td')
                        .text((d, i) => i + 1)
                        .attr('class', 'position');

                    users
                        .append('td')
                        .html (({picture, name, tag}) => `<div><img src="${picture}" alt="Avatar" style="vertical-align:middle"></img><strong style="vertical-align:middle">${name}</strong><span style="vertical-align:middle">#${tag}</span></div>`)
                        .attr('class', 'user');

                    const lvl = users
                        .append('td')
                        .attr('class', 'lvl');

                    lvl
                        .append('span')
                        .html(({level, nextLevelXP, xp}) => `<div class="tooltip">${level}<span class="tooltiptext">` + (nextLevelXP - xp) + ` XP needed for next level.</span></div>`);

                    lvl
                        .append('span')
                        .text(({xp}) => xp + ' XP');

                } else {

                    Swal.fire({
                        type: 'error',
                        title: 'Sorry,',
                        text: 'but i cant find this guild.',
                        showConfirmButton: false,
                        allowEscapeKey: false,
                        allowEnterKey: false,
                        allowOutsideClick: false
                    });

                }

            } else {
                console.error(xhr.statusText);
            }
        }
    };
    xhr.onerror = function (e) {
        console.error(xhr.statusText);
    };
    xhr.send(null);
}
