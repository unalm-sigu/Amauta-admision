new Vue({
    el: '#main',
    data: {
        ticket: {
            id: idticket,
            oficina: {id: null},
            persona: {id: null},
            mensajeTicketAyuda: {},
            mensajesTicketAyuda: [],
            colaborador: {id: null}
        },
        estadoButton: false,
        estadoButtonComentar: false,
        archivo: {},
        archivos: [],
        mensajes: [],
        mensaje: '',
        dataModalAsigar: {
            title: 'Asignar Colaborador',
            showaccept: false
        },
        colaboradorAsignado: {id: null}
    },
    mounted() {
        let vue = this;
        vue.find();
        vue.asignarTicket();

    },
    methods: {
        find() {


            let vue = this;

            $.ajax({
                url: APP.url('atencion/ticket/find'),
                data: {id: vue.ticket.id},
                success: function (response) {
                    if (response.success) {
                        vue.ticket = response.data.ticket;



                    } else {
                        notify(response.message, "error");
                    }
                }, error: function () {
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
        responder() {

            let vue = this;

            if ($('[name="mensaje"]').parsley().validate() != true) {
                console.log('errror');
                return;
            }


            vue.estadoButton = true;



            $.ajax({
                method: 'POST',
                url: APP.url('atencion/ticket/saverespuesta'),
                data: {
                    mensaje: vue.mensaje,
                    'ticketAyuda.id': vue.ticket.id
                },
                success: function (response) {
                    if (response.success) {

                        vue.ticket.mensajesTicketAyuda.push(response.data);
                        vue.mensaje = '';

                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                    vue.estadoButton = false;
                }, error: function () {
                    vue.estadoButton = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });



        },
        asignarTicket() {

            let vue = this;

            if (vue.ticket.colaborador.id) {
                return;
            }

            vue.$refs.modalasignar.open();

            vue.colaboradorAsignado.id = null;
        },
        asignarme() {
            let vue = this;


            swal('¿Seguro que desea asignarse el ticket de ayuda?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {


                if (value != true) {
                    return;
                }

                $.ajax({
                    method: 'POST',
                    async: false,
                    url: APP.url('atencion/ticket/asignarme'),
                    data: {id: vue.ticket.id},
                    success: function (response) {
                        if (response.success) {

                            vue.find();
                            vue.$refs.modalasignar.close();

                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});

                        } else {


                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});

                        }
                    },
                    error: function () {
                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });



            }).catch(err => {
                swal(APP.errorComunicacion, "error");
            });



        },
        asignarColaboradorSelect() {
            let vue = this;




            swal('¿Seguro que desea asignar el ticket de ayuda al colaborador seleccionado?', {
                icon: "warning",
                closeOnClickOutside: false,
                closeOnEsc: false,
                dangerMode: true,
                buttons: {
                    cancel: {text: "Cancelar", closeModal: true, visible: true},
                    confirm: {text: "Aceptar", closeModal: false}
                }
            }).then((value) => {

                if (value != true) {
                    return;
                }

                $.ajax({
                    method: 'POST',
                    async: false,
                    url: APP.url('atencion/ticket/asignarColaborador'),
                    data: {
                        id: vue.ticket.id,
                        'colaborador.id': vue.colaboradorAsignado.id
                    },
                    success: function (response) {
                        if (response.success) {

                            vue.find();

                            vue.$refs.modalasignar.close();

                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});

                        } else {

                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});

                        }
                    },
                    error: function () {

                        return  swal({text: MESSAGES.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });



            }).catch(err => {
                swal(APP.errorComunicacion, "error");
            });


        },
        comentar() {

            let vue = this;

            if ($('[name="mensaje"]').parsley().validate() != true) {
                console.log('errror');
                return;
            }


            vue.estadoButtonComentar = true;

            $.ajax({
                method: 'POST',
                url: APP.url('atencion/ticket/savenota'),
                data: {
                    mensaje: vue.mensaje,
                    'ticketAyuda.id': vue.ticket.id
                },
                success: function (response) {
                    if (response.success) {

                        vue.ticket.mensajesTicketAyuda.push(response.data);
                        vue.estadoButtonComentar = '';

                        notify(response.message, "info");
                    } else {
                        notify(response.message, "error");
                    }
                    vue.estadoButtonComentar = false;
                }, error: function () {
                    vue.estadoButtonComentar = false;
                    notify(MESSAGES.errorComunicacion, "error");
                }
            });

        },
    }
});