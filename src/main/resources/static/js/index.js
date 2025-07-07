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

            echoLogs.getPrevLog();

            echoLogs.bindInputEvents();
            echoLogs.connectSSE(currentPath);
        },
        getPrevLog : function() {
            let path = currentPath;
            let url = "/api/history?path=" + encodeURIComponent(path) + "&limit=100";
            fetch(url)
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Network response was not ok');
                    }
                })
                .then(data => {
                    console.log(data);
                    if (data && data.logs) {
                        data.logs.forEach(log => {
                            let hour = log.created_at.substring(11, 13).padStart(2, '0');
                            let minute = log.created_at.substring(14, 16).padStart(2, '0');
                            let msg = log.message;
                            echoLogs.appendLog_withHourMinute(hour, minute, msg);
                        });
                    } else {
                        echoLogs.appendLog('> no logs found');
                    }
                })
                .catch(error => {
                    console.error('Error fetching logs:', error);
                });
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

            // input이 포커스 안되어 있으면 자동으로 포커스
            document.addEventListener('click', function(e) {
                if (document.activeElement !== input) {
                    input.focus();
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
                // echoLogs.appendLog('> commands: /cd /light /dark /ls /mkdir /tree /echo /help');
                echoLogs.appendLog('> commands: /light /dark /help')
            }
            /*
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
             */
            else {
                echoLogs.echo(cmd);
            }
        },
        cd : function(path) {
            console.log(`Changing directory to: ${path}`);
            // delete sse connection
            echoLogs.eventSource.close();
            // eval valid path
            let data = {
                "path" : currentPath,
                "command": "/cd " + path
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
                    echoLogs.appendLog('> error changing directory');
                }
            }).then(data => {
                console.log(data);
                // reconnect sse with new path
                let responsePath = data.currentPath.name;
                if (responsePath == "undefined" || responsePath == null) {
                    echoLogs.appendLog('> invalid path');
                    return;
                }else{
                    if (responsePath == "") { path = "/"; }
                    else { path = responsePath; }
                    echoLogs.connectSSE(path);
                    currentPath = path;
                    pathElem.textContent = currentPath;
                }
            }).catch(error => {
                console.error('Error:', error);
            });
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
            console.log(data);
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
                    echoLogs.appendLog('> error creating directory');
                }
            }).then(data => {
                echoLogs.appendLog(`> created directory ${path}`);
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
                echoLogs.drawDirTree(data);
            }).catch(error => {
                console.error('Error:', error);
            });
        },
        drawDirTree : function(data) {
            console.log(data);
            let tree = data.tree;
            /*
                { children: [
                        { children : [], name : "dir1", dir_id : 2 },
                    ], name : "", dir_id : 1 }
             */
            echoLogs.recursiveTree(tree, 0);
        },
        recursiveTree : function(node, depth) {
            if(node.name == "") { node.name = "root"; }
            let indent = ' '.repeat(depth * 2);
            echoLogs.appendLog(`${indent}L${node.name}`);
            if (node.children && node.children.length > 0) {
                node.children.forEach(child => {
                    echoLogs.recursiveTree(child, depth + 1);
                });
            }
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
        appendLog_withHourMinute : function(hour, minute, cmd) {
            // append log without date
            logs.textContent += `\n >[${hour}:${minute}] ${cmd}`;
            logs.scrollTop = logs.scrollHeight;
        },
        currentDateTime : function() {
            let now = new Date();
            // format date and time hh:mm
            let hours = String(now.getHours()).padStart(2, '0');
            let minutes = String(now.getMinutes()).padStart(2, '0');
            return `${hours}:${minutes}`;
        }
    }
})();