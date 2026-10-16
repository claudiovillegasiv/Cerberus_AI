document.addEventListener('DOMContentLoaded', function () {

    // 1. CONFIGURACIÓN Y REFERENCIAS AL DOM
    var BACKEND_URL = 'http://' + window.location.hostname + ':8080/api/agente/procesar';

    // Elementos del Chat
    var chatForm = document.getElementById('chat-form');
    var userInput = document.getElementById('user-input');
    var messagesList = document.getElementById('messages-list');
    var welcomeScreen = document.getElementById('welcome-screen');
    var chatContainer = document.getElementById('chat-container');

    // Elementos de la Barra Lateral (Sidebar)
    var appWrapper = document.getElementById('app-wrapper');
    var btnSidebar = document.getElementById('btn-sidebar');
    var btnCloseSidebar = document.getElementById('btn-close-sidebar');

    // Elementos del Modal de Agentes
    var btnAgentes = document.getElementById('btn-agentes');
    var agentsModal = document.getElementById('agents-modal');
    var closeModalBtn = document.getElementById('close-modal-btn');

    // 2. CONTROL DEL FORMULARIO Y TEXTAREA
    // Auto-resize y envío con la tecla Enter en el Textarea
    if (userInput) {
        userInput.addEventListener('input', function () {
            this.style.height = 'auto';
            this.style.height = this.scrollHeight + 'px';

            if (this.scrollHeight > 50) {
                this.style.borderRadius = '16px';
            } else {
                this.style.borderRadius = '24px';
            }
        });

        userInput.addEventListener('keydown', function (e) {
            if (e.key === 'Enter' && !e.shiftKey) {
                e.preventDefault();
                chatForm.dispatchEvent(new Event('submit'));
            }
        });
    }

    // Envío del mensaje
    if (chatForm) {
        chatForm.addEventListener('submit', function (e) {
            e.preventDefault();

            var promptText = userInput.value.trim();
            if (!promptText) return;

            // Resetear el textarea
            userInput.value = '';
            userInput.style.height = 'auto';
            userInput.style.borderRadius = '24px';
            
            var estrellaCaracter = "✦";
            
            if (promptText.includes(estrellaCaracter)) {
                
                // 1. Evitamos que el cohete despegue si está visible
                // y que se envíe nada al backend.

                // 2. Activamos la animación Disco en el body
                document.body.classList.add('disco-active');

                // 4. Establecemos un temporizador para quitar el efecto (2.5 segundos)
                setTimeout(function() {
                    document.body.classList.remove('disco-active');
                }, 2500); 

                // 5. IMPORTANTE: Usamos 'return' para salir de la función aquí.
                // Todo lo que sigue (despegue, envío al backend) NO se ejecutará.
                return;
            }

            // Animación inicial de despegue (si la pantalla de bienvenida está activa)
            if (welcomeScreen && welcomeScreen.style.display !== 'none') {
                welcomeScreen.classList.add('launching');
                document.body.classList.add('space-mode');

                setTimeout(function () {
                    welcomeScreen.style.display = 'none';
                    welcomeScreen.classList.remove('launching');
                    chatContainer.style.justifyContent = 'flex-start';

                    procesarEnvioMensaje(promptText);
                }, 2500);

            } else {
                procesarEnvioMensaje(promptText);
            }
        });
    }

    function procesarEnvioMensaje(promptText) {
        appendMessage(promptText, 'user', false);
        var loadingMessageId = appendMessage('El orquestador está procesando tu petición...', 'agent', true);
        enviarPeticionBackend(promptText, loadingMessageId);
    }

    // 3. INTERACCIÓN CON EL BACKEND (SPRING BOOT)
    function enviarPeticionBackend(promptText, loadingMessageId) {
        fetch(BACKEND_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ prompt: promptText })
        })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Error en el servidor: ' + response.status);
            }
            return response.json();
        })
        .then(function (data) {
            updateMessage(loadingMessageId, data.respuesta, data.clasificacion);
        })
        .catch(function (error) {
            console.error('Error al conectar con el servidor:', error);
            updateMessage(loadingMessageId, '⚠️ Ocurrió un error al comunicarse con el agente orquestador.', null);
        });
    }

    // 4. RENDERING Y MANEJO DE MENSAJES EN LA UI
    function appendMessage(text, sender, isLoading) {
        var msgId = 'msg-' + Date.now();
        var msgDiv = document.createElement('div');
        msgDiv.classList.add('message', sender);
        msgDiv.id = msgId;

        if (isLoading) {
            msgDiv.style.fontStyle = 'italic';
            msgDiv.style.opacity = '0.7';
        }

        msgDiv.textContent = text;
        messagesList.appendChild(msgDiv);
        scrollToBottom();

        return msgId;
    }

    function updateMessage(msgId, text, clasificacion) {
        var msgDiv = document.getElementById(msgId);
        if (!msgDiv) return;

        msgDiv.style.fontStyle = 'normal';
        msgDiv.style.opacity = '1';
        msgDiv.innerHTML = '';

        if (clasificacion && clasificacion.intencion) {
            var badge = document.createElement('div');
            badge.classList.add('meta-badge');
            badge.textContent = 'Agente: ' + clasificacion.intencion;
            msgDiv.appendChild(badge);
        }

        var textDiv = document.createElement('div');
        textDiv.textContent = text;
        msgDiv.appendChild(textDiv);

        scrollToBottom();
    }

    function scrollToBottom() {
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    // 5. COMPONENTES INTERACTIVOS (SIDEBAR Y MODAL)
    // Sidebar de Historial
    if (btnSidebar && appWrapper) {
        btnSidebar.addEventListener('click', function () {
            appWrapper.classList.toggle('sidebar-open');
        });
    }

    if (btnCloseSidebar && appWrapper) {
        btnCloseSidebar.addEventListener('click', function () {
            appWrapper.classList.remove('sidebar-open');
        });
    }

    // Pop-up Modal de Agentes
    if (btnAgentes && agentsModal) {
        btnAgentes.addEventListener('click', function (e) {
            e.stopPropagation();
            agentsModal.classList.toggle('active');
        });

        if (closeModalBtn) {
            closeModalBtn.addEventListener('click', function () {
                agentsModal.classList.remove('active');
            });
        }

        document.addEventListener('click', function (e) {
            if (!agentsModal.contains(e.target) && !btnAgentes.contains(e.target)) {
                agentsModal.classList.remove('active');
            }
        });
    }
});