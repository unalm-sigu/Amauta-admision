Vue.component("multiselect", window.VueMultiselect.default)

new Vue({
    el: '#consejeriaVUE',
    data: {
        bgColorClass: {sinconsejero: '', activo: ''},
        aconsejadosURL: APP.url(rutaModulo + '/list'),
        ciclo: JSON.parse(cicloJson),
        isLoading: false,
        consejeroModal: {
            id: 'consejeroModal',
            header: true,
            title: "Tutores",
            okbtn: 'Aceptar',
            showaccept: true
        },
        carreraSelect: {},
        consejeros: [],
        seleccionado: '',
        alumnoConsejeroForm: {},
        count: {matriculados: 0, noMatriculados: 0, retiroCiclo: 0}
    },
    mounted: function () {
        let $vue = this;
        $vue.countData();
        let query = $vue.$refs.load.getParameterByName('queries[estado]');
        query = (query == null) ? '' : query;
        if (query != '') {
            $vue.$refs.load.querie.push({name: 'estado', value: query});
            $vue.$refs.load.repreload();
        }
    },
    methods: {
        findAconsejado(tipo) {
            let $vue = this;
            $vue.$refs.load.querie = [];
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'estado', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;
                $vue.$refs.load.querie.push({name: 'estado', value: tipo});
            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';
                $vue.$refs.load.changeUrl('queries[estado ]', null);
            }
            $vue.$refs.load.loadRemoteData();
        },
        countData() {
            let $vue = this;
            $vue.isLoading = true;
            $.ajax({
                url: APP.url(rutaModulo + "/countData"),
                data: {idCarrera: $vue.carreraSelect.id},
                dataType: 'json',
                type: 'post',
            }).then(response => {
                $vue.count = response.data;
            });
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        matriculatAutoriazacion(item) {
            let $vue = this;
            var texto;
            (item.estadoMatriculaAutorizacion == false) ? texto = "habilitar" : texto = "inhabilitar";
            var matriculaAutorizacion = !item.estadoMatriculaAutorizacion;

            this.isLoading = true;
            bootbox.confirm({
                message: '¿Esta seguro que desea ' + texto + ' la matricula del alumno seleccionado? ',
                buttons: {
                    confirm: {label: 'Aceptar', className: "btn-success"},
                    cancel: {label: 'Cancelar', className: "btn-link"}
                },
                callback: function (result) {
                    if (result) {
                        $.ajax({
                            method: 'POST',
                            url: APP.url(rutaModulo + "/matriculaAutorizacion"),
                            data: JSON.stringify({
                                alumno: item.alumno,
                                autorizacionMatricula: matriculaAutorizacion
                            }),
                            contentType: "application/json",
                            success: function (response) {
                                if (response.success) {
                                    notify(response.message, 'info');
//                                    $("#chkbox").prop("checked", matriculaAutorizacion);
                                    $vue.$refs.load.loadRemoteData();
                                    this.isLoading = false;
                                } else {
                                    notify(response.message, 'error');
                                }
                            }, error: function () {
                                notify(Messages.errorComunicacion, "error");
                            }
                        });
                    }
                }
            });
        },
        solicitudBeneficio(item) {
            let $vue = this;

            var sexo = item.alumno.persona.sexo == 'M' ? 'al alumno ' : 'a la alumna ';
            var alumno = sexo + item.alumno.persona.apellidosNombres;
            var ciclo = item.cicloAcademico.descripcion;


            swal('¿Esta seguro que desea asignar el beneficio de último ciclo ' + alumno + ' en el ciclo ' + ciclo + ' ?', {
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
                    url: APP.url(rutaModulo + "/solicitudBeneficio"),
                    data: JSON.stringify(item),
                    contentType: "application/json",
                    success: function (response) {
                        if (response.success) {
                            $vue.$refs.load.loadRemoteData();
                            return  swal({text: response.message, icon: "success", button: false, timer: 1000});
                        } else {
                            return  swal({text: response.message, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                        }
                    },
                    error: function () {
                        return  swal({text: Messages.errorComunicacion, icon: "error", dangerMode: true, button: {text: "Aceptar"}});
                    }
                });
            }).catch(err => {
                if (err) {
                    swal(APP.errorComunicacion, "error");
                } else {
                    swal.stopLoading();
                    swal.close();
                }
            });

        }
    }
});







        