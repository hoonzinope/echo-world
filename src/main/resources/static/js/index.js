(function() {
    let logs;
    let input;
    let pathElem;
    let currentPath = '/';
    let theme = 'dark';

    document.addEventListener( 'DOMContentLoaded', function() {
        echoLogs.init();
    });

    let echoLogs = {
        init : function () {
            console.log( 'Echo Logs Initialized' );

            logs = document.getElementById('logs');
            input = document.getElementById('cmdInput');
            pathElem = document.getElementById('currentPath');
            currentPath = '/';
            theme = 'dark';

            echoLogs.bindInputEvents();
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
                        echoLogs.appendLog(`[${new Date().toLocaleTimeString()}] ${cmd}`);
                        // TODO: POST to /api/log
                    }
                }
            });
        },
        handleCommand : function(cmd) {
            if (cmd === '/light') {
                document.body.classList.add('light');
                theme = 'light';
                echoLogs.appendLog('> light mode on');
            } else if (cmd === '/dark') {
                document.body.classList.remove('light');
                theme = 'dark';
                echoLogs.appendLog('> dark mode on');
            } else if (cmd === '/help') {
                echoLogs.appendLog('> commands: /cd /light /dark /ls /mkdir /pwd /tree /echo /help');
            }
        },
        appendLog : function(cmd) {
            logs.textContent += `\n${cmd}`;
            logs.scrollTop = logs.scrollHeight;
        }
    }
})();