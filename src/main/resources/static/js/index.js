(function() {
    let logs;
    let input;
    let pathElem;
    let currentPath = '/';
    let theme = 'dark';

    document.addEventListener( 'DOMContentLoaded', function() {
        echoLogs.init();
    });
    // TODO : directory cd, ls, tree, mkdir error handling
    let echoLogs = {
        eventSource : null,
        init : function () {
            console.log( 'Echo Logs Initialized' );

            logs = document.getElementById('logs');
            input = document.getElementById('cmdInput');
            pathElem = document.getElementById('currentPath');
            currentPath = '/';
            theme = 'dark';

            echoLogs.bindInputEvents();
            echoLogs.connectSSE(currentPath);
        },
        connectSSE : function(path) {
            let url = "/api/stream/logs?path=" + encodeURIComponent(path);
            echoLogs.eventSource = new EventSource(url);
            echoLogs.eventSource.onmessage = function(event) {
                echoLogs.appendLog(event.data);
            };
        },
        bindInputEvents : function() {
            input.focus();
            input.addEventListener('keydown', (e) => {
                if (e.key === 'Enter') {
                    const cmd = input.value.trim();
                    input.value = '';

                    if (cmd.startsWith('/')) {
                        echoLogs.handleCommand(cmd);
                    } else {
                        // TODO: POST to /api/log
                        echoLogs.echo(cmd);
                    }
                }
            });
        },
        handleCommand : function(cmd) {
            if (cmd === '/light') {
                document.body.classList.add('light');
                theme = 'light';
                echoLogs.appendLog('> light mode on');
            }
            else if (cmd === '/dark') {
                document.body.classList.remove('light');
                theme = 'dark';
                echoLogs.appendLog('> dark mode on');
            }
            else if (cmd === '/help') {
                echoLogs.appendLog('> commands: /cd /light /dark /ls /mkdir /tree /echo /help');
            }
            else if (cmd.startsWith('/cd ')) {
                let path = cmd.substring(4).trim();
                if (path) {
                    // change directory
                    echoLogs.cd(path);
                    echoLogs.appendLog(`> changed directory to ${path}`);
                } else {
                    echoLogs.appendLog('> usage: /cd <path>');
                }
            }
            else if (cmd === '/ls') {
                echoLogs.ls();
            }
            else if (cmd.startsWith('/mkdir ')) {
                let dirName = cmd.substring(7).trim();
                echoLogs.mkdir(dirName);
            }
            else if (cmd === '/pwd') {
                echoLogs.pwd();
            }
            else if (cmd === '/tree') {
                echoLogs.tree();
            }
            else if (cmd.startsWith('/echo ')) {
                let text = cmd.substring(6).trim();
                echoLogs.echo(text);
            }
            else {
                echoLogs.echo(cmd);
            }
        },
        cd : function(path) {
            console.log(`Changing directory to: ${path}`);
            // delete sse connection
            echoLogs.eventSource.close();
            // reconnect sse with new path
            echoLogs.connectSSE(path);
            // change currentpath
            currentPath = path;
            pathElem.textContent = currentPath;
        },
        ls : function() {
            // show current directory contents
            let data = {
                path : currentPath,
                command: "/ls"
            };
            let url = "/api/cmd";
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    echoLogs.appendLog('> error listing directory contents');
                }
            }).then(data => {
                console.log(data);
            }).catch(error => {
                console.error('Error:', error);
            });
        },
        mkdir : function(path) {
            // create a new directory
            if (!path) {
                echoLogs.appendLog('> usage: /mkdir <directory_name>');
                return;
            }
            let data = {
                path: currentPath,
                command: "/mkdir " + path
            };
            let url = "/api/cmd";
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(response => {
                if (response.ok) {
                    echoLogs.appendLog(`> created directory ${path}`);
                    if(!currentPath.endsWith("/")) { path = currentPath + "/" + path;}
                    else { path = currentPath + path; }
                    currentPath = path;
                } else {
                    echoLogs.appendLog('> error creating directory');
                }
            }).catch(error => {
                console.error('Error:', error);
            });
        },
        pwd : function() {
            echoLogs.appendLog(`> ${currentPath}`);
        },
        tree : function() {
            // display directory tree structure
            let data = {
                path : currentPath,
                command: "/tree"
            };
            let url = "/api/cmd";
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else {
                    echoLogs.appendLog('> error displaying directory tree');
                }
            }).then(data => {
                console.log(data);
            }).catch(error => {
                console.error('Error:', error);
            });
        },
        echo : function(text) {
            // add new log
            let data = {
                path : currentPath,
                message : text
            }
            let url = "/api/appendLog"
            fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            }).then(response => {
                if (response.ok) {
                    console.log('Success');
                }
            }).catch(error => {
                console.error('Error:', error);
            });
        },
        appendLog : function(cmd) {
            //current datetime
            let datetime = echoLogs.currentDateTime();
            // append log
            logs.textContent += `\n > [${datetime}] ${cmd}`;
            logs.scrollTop = logs.scrollHeight;
        },
        currentDateTime : function() {
            let now = new Date();
            let datetime = now.toLocaleString('en-US', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
            // format datetime
            datetime = datetime.replace(',', '');
            datetime = datetime.replace(/\//g, '-');
            datetime = datetime.replace(/ /g, 'T');
            datetime = datetime.replace(/:/g, '-');
            return datetime;
        }
    }
})();