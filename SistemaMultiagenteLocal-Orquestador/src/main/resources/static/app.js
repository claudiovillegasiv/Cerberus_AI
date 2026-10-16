document.addEventListener('DOMContentLoaded', function () {

    // 1. CONFIGURACIÓN Y REFERENCIAS AL DOM
    // Ruta relativa porque el front y el back viven en el mismo servidor (Spring Boot)
    var BACKEND_URL = '/api/agente/procesar';

    // Elementos del Chat
    var chatForm = document.getElementById('chat-form');
    var userInput = document.getElementById('user-input');
    var messagesList = document.getElementById('messages-list');
    var welcomeScreen = document.getElementById('welcome-screen');
    var chatContainer = document.getElementById('chat-container');

    // Nuevos Elementos para Adjuntar Archivos
    var fileInput = document.getElementById('file-input');
    var btnAttach = document.getElementById('btn-attach');

    // Elementos de la Barra Lateral (Sidebar)
    var appWrapper = document.getElementById('app-wrapper');
    var btnSidebar = document.getElementById('btn-sidebar');
    var btnCloseSidebar = document.getElementById('btn-close-sidebar');

    // Elementos del Modal de Agentes
    var btnAgentes = document.getElementById('btn-agentes');
    var agentsModal = document.getElementById('agents-modal');
    var closeModalBtn = document.getElementById('close-modal-btn');

    // 2. CONTROL DE EVENTOS PARA ADJUNTAR ARCHIVOS
    if (btnAttach && fileInput) {
        btnAttach.addEventListener('click', function() {
            fileInput.click();
        });

        // Cambiar el color del ícono cuando hay un archivo cargado
        fileInput.addEventListener('change', function() {
            if (this.files.length > 0) {
                btnAttach.style.color = '#4CAF50'; 
                btnAttach.title = 'Archivo cargado: ' + this.files[0].name;
            } else {
                btnAttach.style.color = '#a0a0a0';
                btnAttach.title = 'Adjuntar archivo';
            }
        });
    }

    // 3. CONTROL DEL FORMULARIO Y TEXTAREA
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

    if (chatForm) {
        chatForm.addEventListener('submit', function (e) {
            e.preventDefault();

            var promptText = userInput.value.trim();
            if (!promptText && (!fileInput || fileInput.files.length === 0)) return; // Validar que haya texto o archivo

            userInput.value = '';
            userInput.style.height = 'auto';
            userInput.style.borderRadius = '24px';
            
            var estrellaCaracter = "✦";
            
            if (promptText.includes(estrellaCaracter)) {
                document.body.classList.add('disco-active');
                setTimeout(function() {
                    document.body.classList.remove('disco-active');
                }, 2500); 
                return;
            }

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

    // 4. INTERACCIÓN CON EL BACKEND (SPRING BOOT) USANDO FORMDATA
    function enviarPeticionBackend(promptText, loadingMessageId) {
        var formData = new FormData();
        formData.append('prompt', promptText);

        if (fileInput && fileInput.files.length > 0) {
            formData.append('archivo', fileInput.files[0]);
        }

        fetch(BACKEND_URL, {
            method: 'POST',
            body: formData 
        })
        .then(function (response) {
            if (!response.ok) {
                throw new Error('Error en el servidor: ' + response.status);
            }
            return response.text(); 
        })
        .then(function (textoRespuesta) {
            // Limpiar el input de archivo y resetear el botón
            if (fileInput) {
                fileInput.value = '';
                btnAttach.style.color = '#a0a0a0';
                btnAttach.title = 'Adjuntar archivo';
            }
            updateMessage(loadingMessageId, textoRespuesta, null);
        })
        .catch(function (error) {
            console.error('Error al conectar con el servidor:', error);
            updateMessage(loadingMessageId, '⚠️ Ocurrió un error al comunicarse con el agente orquestador.', null);
        });
    }

    // 5. RENDERING Y MANEJO DE MENSAJES EN LA UI
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

        // Renderizado del código con marcado (marked.js)
        var textDiv = document.createElement('div');
        if (typeof marked !== 'undefined') {
            textDiv.innerHTML = marked.parse(text);
        } else {
            // Fallback por si la librería no carga
            textDiv.style.whiteSpace = 'pre-wrap';
            textDiv.textContent = text;
        }
        msgDiv.appendChild(textDiv);

        scrollToBottom();
    }

    function scrollToBottom() {
        chatContainer.scrollTop = chatContainer.scrollHeight;
    }

    // 6. COMPONENTES INTERACTIVOS (SIDEBAR Y MODAL)
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