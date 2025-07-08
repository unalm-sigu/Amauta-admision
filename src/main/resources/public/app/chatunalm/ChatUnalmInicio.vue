<template>
    <div>

        <div class="navbar navbar-expand-lg" :style="esTest ? { backgroundColor: '#b53346' } : {}">

            <div class="navbar-text-chat flex text-right" :style="esTest ? { backgroundColor: '#b53346' } : {}" id="pageTitle">
                &nbsp;&nbsp;
            </div>

            <ul class="nav flex-row order-lg-2">
                <li class="nav-item dropdown">
                    <a class="nav-link px-3" data-toggle="dropdown">
                        <i class="fa fa-bell text-muted"></i>
                        <span v-bind:class="classCantidad"
                              class="badge badge-pill up-chat">{{asuntos.length}}</span>
                    </a>
                    <div class="dropdown-menu dropdown-menu-chat dropdown-menu-right w-md animate fadeIn mt-2 p-0">
                        <div class="scrollable hover" style="max-height: 250px">
                            <div class="list">

                                <template v-for="(item,idx) in asuntos">

                                    <div class="list-item " data-id="item-6">
                                        <span class="w-24-chat avatar-chat circle-chat brown">
                                            <span class="fa fa-envelope"></span>
                                        </span>
                                        <div class="list-body">
                                            <a v-on:click.prevent="verMensajes(item,idx)" class="item-title _500">
                                                {{item.mensajePrincipal.remitente.persona.nomPaterno}}
                                            </a>

                                            <div class="item-except text-sm text-muted h-1x">
                                                {{item.asunto}}
                                            </div>

                                            <div class="item-tag tag hide">
                                            </div>
                                        </div>
                                        <div>
                                            <span v-if="item.mensajePrincipal.esHoy" class="item-date text-xs text-muted">
                                                {{item.mensajePrincipal.hora}}
                                            </span>
                                            <span v-else="" class="item-date text-xs text-muted">
                                                {{item.mensajePrincipal.fechaCorta}}
                                            </span>
                                        </div>
                                    </div>
                                </template>

                            </div>
                        </div>

                        <!--div class="d-flex px-3 py-2 b-t">
                            <div class="flex">
                                <span>9 Notificaciones</span>
                            </div>
                            <a href="setting.html">Ver todo <i class="fa fa-angle-right text-muted"></i></a>
                        </div-->
                    </div>
                </li>

                <li class="dropdown d-flex align-items-center m-b-xxs">
                    <a href="#" data-toggle="dropdown" class="d-flex align-items-center link-chat m-b-xxs m-r">
                        <span class="w-28-chat avatar-chat circle-chat green">
                            {{persona.avatar}}
                        </span>
                        <span class="ml-2"> {{persona.nombreCompleto}} </span>
                    </a>
                </li>

            </ul>

        </div>

        <!--<modal-base></modal-base>-->
        <modal-mensaje ref="modalMensaje"></modal-mensaje>
    </div>

</template>
<script>


    const ModalMensaje = httpVueLoader('./ModalMensaje.vue');

    module.exports = {
        components: {
            ModalMensaje
        },

        data() {
            return {
                existeWs: false,
                esTest: true,
                rutaModulo: `mensajeria/chatunalm`,
                docente: {modalidadEstudio: {}, departamentoAcademico: {facultad: {}}},
                persona: {tipoDocumento: {}},
                asuntos: [],
                idModalMensaje: "id-modal-mensaje-chat-unalm"
            };
        },
        mounted() {
            this.loadData();
            mySounds.loadSounds();
        },

        computed: {
            classCantidad() {
                if (this.asuntos.length == 0) {
                    return "";
                }
                return "danger";
            }
        },

        methods: {
            loadData() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${this.rutaModulo}/allData`
                })).then((resp) => {
                    this.esTest = resp.data.data.esTest;
                    this.asuntos = resp.data.data.asuntos;
                    this.docente = resp.data.data.docente;
                    this.persona = resp.data.data.persona;
                    if (!this.existeWs) {
                        this.connectSocket();
                    }
                });
            },
            verMensajes(item, index) {

                let config = {
                    id: this.idModalMensaje,
                    message: item.mensajePrincipal.mensaje,
                    okbtn: "Aceptar",
                    okclass: "btn-primary"
                };
                
                this.$refs.modalMensaje.open(config, item, this.asuntos, index, this.rutaModulo);

                return;
                //Swal.fire({
                swal({
                    showCancelButton: false,
                    title: item.asunto,
                    html: item.mensajePrincipal.mensaje,
                    cancelButtonText: 'Cerrar',
                    confirmButtonText: '<i class="fa fa-thumbs-up"></i> Aceptar'
                }).then((result) => {
                    console.log("result=", result);
                    if (result.value) {
                        myUtils.axios(VUE_AXIOS.structGetData({
                            url: `/${this.rutaModulo}/marcarMensaje`,
                            body: {id: item.mensajePrincipal.id}
                        })).then(() => {
                            mySounds.playAudio('SINGLE');
                            if (item.mensajes.length == 1) {
                                this.asuntos.splice(index, 1);
                            } else {
                                item.mensajes.splice(0, 1);
                                item.mensajePrincipal = item.mensajes[0];
                            }
                        });


                    }
                });
            },
            addMensaje(mensajeNuevo) {
                let asuntoNuevo = mensajeNuevo.asuntoMensaje;
                let asuntoBase = this.asuntos.find(a => a.id == asuntoNuevo.id);
                if (asuntoBase) {
                    if (mensajeNuevo.estado === 'ENVIADO') {
                        asuntoBase.asunto = asuntoNuevo.asunto;
                        asuntoBase.mensajePrincipal = mensajeNuevo;
                        asuntoBase.mensajes.unshift(mensajeNuevo);
                        mySounds.playAudio('DING');

                    } else if (mensajeNuevo.estado === 'ANULADO') {
                        let nuevosMsgs = asuntoBase.mensajes.filter(m => m.id != mensajeNuevo.id);
                        if (nuevosMsgs.length == 0) {
                            let nuevosAsuntos = this.asuntos.filter(a => a.id != asuntoBase.id);
                            this.asuntos = nuevosAsuntos;

                        } else {
                            let nuevoMsg = nuevosMsgs[0];
                            asuntoBase.mensajePrincipal = nuevoMsg;
                            asuntoBase.mensajes = nuevosMsgs;
                        }


                    }
                    return;

                } else {
                    asuntoNuevo.mensajePrincipal = mensajeNuevo;
                    asuntoNuevo.mensajes = [];
                    asuntoNuevo.mensajes.push(mensajeNuevo);
                    this.asuntos.unshift(asuntoNuevo);
                    mySounds.playAudio('DING');
                }
            },

            // metodos sockets
            connectSocket() {
                let vue = this;
                vue.existeWs = true;
                const socket = new SockJS('/wsconnect');
                const stompClient = Stomp.over(socket);

                stompClient.connect({}, function (frame) {
                    let canal = `/monitoreo/chatunalm/d${vue.persona.codigo}@unalm.edu.pe`;
                    if (vue.docente.id) {
                        canal = `/monitoreo/chatunalm/d${vue.docente.codigo}@unalm.edu.pe`;
                    }
                    stompClient.subscribe(canal, function (messageOutput) {
                        var mensajeChat = JSON.parse(messageOutput.body);
                        vue.addMensaje(mensajeChat);
                    });
                });

                socket.onclose = (event) => {
                    console.log("Conexión websocket cerrada con el código " + event.code + " y la razón es " + event.reason);
                    setTimeout(() => vue.connectSocket(), 60 * 1000);
                };
                socket.onerror = (event) => {
                    console.log("Se ha producido un error" + event);
                };
            },

            // genericos
            commas: myUtils.commas
        }
    };

</script>