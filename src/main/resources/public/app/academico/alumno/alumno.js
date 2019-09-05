new Vue({
    el: '#alumnosVUE',
    data: {
        alumnosURL: APP.url('academico/alumno/list'),
        seleccionado: '',
        accesoEspecialRequest: {correo: null, contraseña: null, dni: null, isNuevo: true, alumno: {}},
        usuario: {},
        bgColorClass: {pregrado: '', postgrado: '', visitante: '', especial: ''},
        arrayPalabra: ["saab", "volvo", "azus", "moldavia", "guinea", "somalia", "meijiEdo", "sapporo", "leopardo", "husky", "azulruso", "mitsubishi", "mercedes", "marilyn", "loreto"],
        modalAsignarAccesoEspecial: {
            id: 'modalAsignarAccesoEspecial',
            header: true,
            title: "Asignar Código Especial",
            okbtn: 'Guardar',
            cancelbtn: 'Cancelar',
            cancelclass: 'btn btn-link',
            showaccept: true,
            modalsize: 'modal-medium',
            form: "formAsigAE"
        }
    },
    mounted: function () {
        let $vue = this;
        let tipo = $vue.$refs.load.getParameterByName('queries[moe.codigo]');
        tipo = (tipo == null) ? '' : tipo;
        if (tipo != '') {
            $vue.bgColorClass[tipo] = 'bg-light';
            $vue.seleccionado = tipo;
            $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
        }
        $vue.$refs.load.repreload();
    },
    methods: {
        verTipoCarrera(item) {
            return (item.carrera.tipo == "MAE" || item.carrera.tipo == "DOC");
        },
        verFacultad(item) {
            return (item.modalidadEstudio.codigo == "PRE" && item.carrera.codigo != item.carrera.facultad.codigo);
        },
        urlAcademico(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/infoacademico') + $vue.getOrigenURL();
        },
        urlDataPersonal(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/fisicoupdate') + $vue.getOrigenURL();
        },
        urlMatricula(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/gomatricula') + $vue.getOrigenURL();
        },
        urlConfigCursos(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/configcursos') + $vue.getOrigenURL();
        },
        urlGoMaipi(item) {
            let $vue = this;
            return APP.url('academico/alumno/' + item.id + '/goMaipi') + $vue.getOrigenURL();
        },
        urlConvalidarTraslado(item) {
            let $vue = this;

            axios.post("/academico/alumno/verificarTramiteTraslado", item)
                    .then(response => {
                        if (response.data.success) {
                            location.href = '/academico/alumno/' + item.id + '/trasladoexterno' + $vue.getOrigenURL();
                        } else {
                            notify("El alumno " + item.persona.apellidosNombres + " no tiene resolución de traslado externo", "warning");
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        asignarAccesoEspecial(item) {
            let $vue = this;

            $vue.accesoEspecialRequest = {alumno: {}, correo: null, contraseña: null, dni: null, isNuevo: true};
            $vue.getUserByPersona(item.persona);

            let alumnoClone = Object.assign({}, item);
            $vue.accesoEspecialRequest.alumno = alumnoClone;
            $vue.accesoEspecialRequest.correo = alumnoClone.persona.email;
            $vue.openMolda('modalAsigAccesoEspecial');
        },
        generatorPass() {
            let $vue = this;
            var aleatorio = Math.floor(Math.random() * 14);
            var numero = Math.floor(Math.random() * 100) + 10;
            return $vue.arrayPalabra[aleatorio] + "" + numero;
        },
        getUserByPersona(persona) {
            let $vue = this;
            axios.post("/academico/alumno/findUsuario", persona)
                    .then(response => {
                        if (response.data.success) {
                            $vue.usuario = response.data.data;
                            if ($vue.usuario.userDni !== undefined) {
//                                $vue.accesoEspecialRequest.dni = $vue.usuario.userDni;
                                $vue.accesoEspecialRequest.isNuevo = false;
                            } else {
//                                $vue.accesoEspecialRequest.dni = $vue.usuario.persona.numeroDocIdentidad;
//                                $vue.accesoEspecialRequest.contraseña = $vue.generatorPass();
                            }
                            $vue.accesoEspecialRequest.contraseña = $vue.generatorPass();
                            $vue.accesoEspecialRequest.dni = $vue.usuario.persona.numeroDocIdentidad;
                            $vue.modalAsignarAccesoEspecial.title = ($vue.accesoEspecialRequest.isNuevo ? " Asignar Código Especial" : " Editar Código Especial");
                            $vue.modalAsignarAccesoEspecial.okbtn = ($vue.accesoEspecialRequest.isNuevo ? " Registrar" : " Actualizar");
                        } else {
                            notify(response.data.message, "warning");
                            $vue.$refs.modalAsignarAccesoEspecial.close();
                        }
                    });
        },
        openMolda(modal) {
            let $vue = this;

            if (modal === "modalAsigAccesoEspecial") {
                $("#" + $vue.modalAsignarAccesoEspecial.form).parsley().destroy();
                $vue.$refs.modalAsignarAccesoEspecial.open();
            }
        },
        saveAccesoEspecial() {
            let $vue = this;
            if ($("#" + $vue.modalAsignarAccesoEspecial.form).parsley().validate() !== true) {
                notify("Debe completar todos los campos requeridos", "error");
                return;
            }

            let clonAccesoEspecialRequest = Object.assign({}, $vue.accesoEspecialRequest);
//            clonAccesoEspecialRequest.contraseña = md5(clonAccesoEspecialRequest.contraseña);
            axios.post("/academico/alumno/saveAccesoEspecial", clonAccesoEspecialRequest)
                    .then(response => {
                        if (response.data.success) {
                            $vue.$refs.modalAsignarAccesoEspecial.close();
                            notify(response.data.message, "success")
                        } else {
                            notify(response.data.message, "warning")
                        }
                    }).catch(e => {
                notify(MESSAGES.errorComunicacion, "error");
            });
        },
        getOrigenURL() {
            var url = window.location.href;
            return "?origen=" + Base64.encode(url);
        },
        isPosgrado(modalidad) {
            return "/EPG/VIS/ESP/".indexOf(modalidad.codigo) >= 0;
        },
        verModalidades(tipo) {
            let $vue = this;
            if ($vue.seleccionado === '') {
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado !== tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.bgColorClass[tipo] = 'bg-light';
                $vue.seleccionado = tipo;

                $vue.$refs.load.querie.push({name: 'moe.codigo', value: tipo});
                $vue.$refs.load.loadRemoteData();

            } else if ($vue.seleccionado !== '' && $vue.seleccionado === tipo) {
                $vue.bgColorClass[$vue.seleccionado] = '';
                $vue.seleccionado = '';

                $vue.$refs.load.querie = [];
                $vue.$refs.load.changeUrl('queries[moe.codigo]', null);
                $vue.$refs.load.loadRemoteData();
            }
        },
        modalAsignarAvance(item) {
            return APP.url('academico/alumno/habilitarCursosHabiles/' + item.id);
        }
    }
});

