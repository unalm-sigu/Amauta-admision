Vue.component("multiselect", window.VueMultiselect.default);
new Vue({
    el: '#grupoVUE',
    data: {
        grupoURL: APP.url(rutaModulo + '/list'),
        tipoGpo: {},
        tipoHorarios: [],
        dias: [],
        horas: [],
        horarioRegular: [],
        horarioGpo: [],
        ciclo: {},
        paginationGpo: {'total-items': 0, 'items-per-page': 12, 'max-size': 3, 'boundary-link-numbers': true},
        grupoActivo: {},
        configCloneCiclo: VUE_MODAL.structFormAjax({
            id: 'modalCloneCiclo',
            title: 'Copiar Ciclo',
            header: true
        }),
        configEditGrupo: VUE_MODAL.structFormAjax({
            id: 'modalEditGrupo',
            title: 'Editar Grupo Horas',
            header: true,
            form: 'formEditGrupo',
            verPaleta: false,
            verLetras: false,
        }),
        grupoHorasForm: {},
        colors: ['#1abc9c', '#e8f8f5', '#d1f2eb', '#a3e4d7', '#76d7c4', '#48c9b0', '#1abc9c', '#17a589', '#148f77', '#117864', '#0e6251', '#16a085', '#e8f6f3', '#d0ece7', '#a2d9ce', '#73c6b6', '#45b39d', '#16a085', '#138d75', '#117a65', '#0e6655', '#0b5345', '#2ecc71', '#eafaf1', '#d5f5e3', '#abebc6', '#82e0aa', '#58d68d', '#2ecc71', '#28b463', '#239b56', '#1d8348', '#186a3b', '#27ae60', '#e9f7ef', '#d4efdf', '#a9dfbf', '#7dcea0', '#52be80', '#27ae60', '#229954', '#1e8449', '#196f3d', '#145a32', '#3498db', '#ebf5fb', '#d6eaf8', '#aed6f1', '#85c1e9', '#5dade2', '#3498db', '#2e86c1', '#2874a6', '#21618c', '#1b4f72', '#2980b9', '#eaf2f8', '#d4e6f1', '#a9cce3', '#7fb3d5', '#5499c7', '#2980b9', '#2471a3', '#1f618d', '#1a5276', '#154360', '#9b59b6', '#f5eef8', '#ebdef0', '#d7bde2', '#c39bd3', '#af7ac5', '#9b59b6', '#884ea0', '#76448a', '#633974', '#512e5f', '#8e44ad', '#f4ecf7', '#e8daef', '#d2b4de', '#bb8fce', '#a569bd', '#8e44ad', '#7d3c98', '#6c3483', '#5b2c6f', '#4a235a', '#34495e', '#ebedef', '#d6dbdf', '#aeb6bf', '#85929e', '#5d6d7e', '#34495e', '#2e4053', '#283747', '#212f3c', '#1b2631', '#2c3e50', '#eaecee', '#d5d8dc', '#abb2b9', '#808b96', '#566573', '#2c3e50', '#273746', '#212f3d', '#1c2833', '#17202a', '#f1c40f', '#fef9e7', '#fcf3cf', '#f9e79f', '#f7dc6f', '#f4d03f', '#f1c40f', '#d4ac0d', '#b7950b', '#9a7d0a', '#7d6608', '#f39c12', '#fef5e7', '#fdebd0', '#fad7a0', '#f8c471', '#f5b041', '#f39c12', '#d68910', '#b9770e', '#9c640c', '#7e5109', '#e67e22', '#fdf2e9', '#fae5d3', '#f5cba7', '#f0b27a', '#eb984e', '#e67e22', '#ca6f1e', '#af601a', '#935116', '#784212', '#d35400', '#fbeee6', '#f6ddcc', '#edbb99', '#e59866', '#dc7633', '#d35400', '#ba4a00', '#a04000', '#873600', '#6e2c00', '#e74c3c', '#fdedec', '#fadbd8', '#f5b7b1', '#f1948a', '#ec7063', '#e74c3c', '#cb4335', '#b03a2e', '#943126', '#78281f', '#c0392b', '#f9ebea', '#f2d7d5', '#e6b0aa', '#d98880', '#cd6155', '#c0392b', '#a93226', '#922b21', '#7b241c', '#641e16', '#ecf0f1', '#fdfefe', '#fbfcfc', '#f7f9f9', '#f4f6f7', '#f0f3f4', '#ecf0f1', '#d0d3d4', '#b3b6b7', '#979a9a', '#7b7d7d', '#bdc3c7', '#f8f9f9', '#f2f3f4', '#e5e7e9', '#d7dbdd', '#cacfd2', '#bdc3c7', '#a6acaf', '#909497', '#797d7f', '#626567', '#95a5a6', '#f4f6f6', '#eaeded', '#d5dbdb', '#bfc9ca', '#aab7b8', '#95a5a6', '#839192', '#717d7e', '#5f6a6a', '#4d5656', '#7f8c8d', '#f2f4f4', '#e5e8e8', '#ccd1d1', '#b2babb', '#99a3a4', '#7f8c8d', '#707b7c', '#616a6b', '#515a5a', '#424949'],
        letras: ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W'],
        configConfirmAction: VUE_MODAL.structConfirm({
            id: "idModalConfirm"
        }),
        orderByLetra: false,
        cfgActivarGrupo: VUE_MODAL.structFormAjax({
            id: 'idModalActivarGpos',
            waiting: false,
        }),
        ocultos: [],
        ocultosSelect: [],
        iniciando: true
    },
    created() {
        this.tipoGpo = JSON.parse(tipoGpoJson);
        this.dias = JSON.parse(diasJson);
        this.horas = JSON.parse(horasJson);
        this.horarioRegular = JSON.parse(horarioRegularJson);
        this.tipoHorarios = JSON.parse(tipoHorariosJson);
        //this.grupoURL = APP.url(rutaModulo + '/list?idTipoGrupo=' + this.tipoGpo.id);
    },
    watch: {
        orderByLetra() {
            let $vue = this;
            $vue.verGruposOrdenados();
        }
    },
    mounted() {
        let $vue = this;
        let orderLetra = $vue.getParameterQuery('order-letra');
        if (orderLetra !== '') {
            $vue.orderByLetra = true;
        } else
            $vue.verGruposOrdenados();
    },
    methods: {
        getParameterQuery(param) {
            let $vue = this;
            let value = $vue.$refs.raptorGrupo.getParameterByName('queries[' + param + ']');
            value = (value === null) ? '' : value;
            return value;
        },
        setParameterQuery(param, value) {
            let $vue = this;
            if (value !== '') {
                $vue.$refs.raptorGrupo.querie.push({name: param, value: value});
            }
        },
        verGruposOrdenados() {
            let $vue = this;
            $vue.$refs.raptorGrupo.querie = [];
            $vue.$refs.raptorGrupo.changeUrl('queries[order-letra]', null);
            $vue.$refs.raptorGrupo.changeUrl('queries[tipo-grupo]', null);

            $vue.setParameterQuery("tipo-grupo", $vue.tipoGpo.id);
            if ($vue.orderByLetra) {
                $vue.setParameterQuery("order-letra", "alfa");
            }
            if ($vue.iniciando) {
                $vue.iniciando = false;
                $vue.$refs.raptorGrupo.repreload();
            } else
                $vue.$refs.raptorGrupo.loadRemoteData();
        },
        styleHdia(dia, hora) {
            let $vue = this;
            let key = dia.id + "-" + hora.id;
            let gpos = $vue.horarioGpo[key];
            if (gpos !== undefined) {
                return "dia-hora-busy";
            }

            let conte = $vue.conteHdia(dia, hora);
            let conCruceRegular = conte.indexOf(", ") >= 0 ? true : false;
            if (conCruceRegular) {
                return "gpo-regular-cruce";
            }
            return "gpo-regular-ok";
        },
        styleConteHdia(dia, hora) {
            let $vue = this;
            let conte = $vue.conteHdia(dia, hora);
            if (conte !== "") {
                return "";
            }
            return "color:#E40DEB;";
        },
        conteHdia(dia, hora) {
            let $vue = this;
            let key = dia.id + "-" + hora.id;
            let gpos = $vue.horarioGpo[key];

            if (gpos !== undefined) {
                return gpos[0].codigo;
            }

            let codes = "";
            let gposReg = $vue.horarioRegular[key];
            if (gposReg !== undefined) {
                for (var i = 0; i < gposReg.length; i++) {
                    codes += (codes === "") ? "" : ", ";
                    codes += gposReg[i].codigo;
                }
            }

            return codes;
        },
        verHorario(item) {
            let $vue = this;
            $vue.grupoActivo = item;

            if ($vue.grupoActivo == undefined) {
                return;
            }
            if ($vue.grupoActivo.id == undefined) {
                return;
            }

            $.ajax({
                url: APP.url(rutaModulo + '/horario'),
                type: 'POST',
                data: {id: item.id},
                success: function (response) {
                    if (response.success) {
                        $vue.horarioRegular = response.data.horarioRegular;
                        $vue.horarioGpo = response.data.horarioGpo;
                        console.log("$vue.horarioRegular = " + $vue.horarioRegular.length)
                        console.log("$vue.horarioGpo = " + $vue.horarioGpo.length)

                    } else {
                        notify(response.message, "error");
                    }
                },
                error: function () {
                    notify(Messages.errorComunicacion, "error");
                }
            });


        },
        styleBorder(item) {
            let $vue = this;
            if (item.id == undefined) {
                if (item.color == undefined) {
                    return "border-color:red; color:black;";
                } else {
                    return "border-color:" + item.color + "; color:black;";
                }
            }
            if (item.id == $vue.grupoActivo.id) {
                return "background-color:#DEBD45; color:white;";
            }
            return "border-color:" + item.color + "; color:black;";
        },
        classHoras(item) {
            if (item.horas == undefined) {
                return "label-danger";
            }
            if (item.horas == 0) {
                return "label-danger";
            }
            return "label-success";
        },
        asignarHora(dia, hora) {
            let $vue = this;
            if ($vue.grupoActivo === null) {
                return;
            }
            if (!$vue.validarDia(dia, hora)) {
                notify("No puede haber horario por intervalos", "error");
                return;
            }

            let data = {dia: dia, hora: hora, grupoHorario: $vue.grupoActivo};
            axios.post('/' + rutaModulo + '/asignarHora', data).then(response => {
                if (response.data.success) {
                    $vue.verHorario($vue.grupoActivo);
                    $vue.$refs.raptorGrupo.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(function (error) {
                notify(Messages.errorComunicacion, "error");
            });
        },
        desasignarHora(dia, hora) {
            let $vue = this;

            if ($vue.grupoActivo == null) {
                return;
            }

            //$vue.data.id = $vue.conterObjHdia(dia, hora).id;
            let gpo = $vue.conterObjHdia(dia, hora);
            if (gpo == null) {
                return;
            }

            let data = {dia: dia, hora: hora, grupoHorario: gpo};
            axios.post('/' + rutaModulo + '/desasignarHora', data).then(response => {
                if (response.data.success) {
                    $vue.verHorario($vue.grupoActivo);
                    $vue.$refs.raptorGrupo.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(function (error) {
                notify(Messages.errorComunicacion, "error");
            });
        },
        conterObjHdia(dia, hora) {
            let $vue = this;
            let key = dia.id + "-" + hora.id;
            let gpos = $vue.horarioGpo[key];
            if (gpos !== undefined) {
                return gpos[0];
            }
            return null;
        },
        validarDia(dia, hora) {
            let $vue = this;
            var res = false;
            var horasGpo = [];
            for (var i = 0; i < $vue.horas.length; i++) {
                let kkeyy = dia.id + "-" + $vue.horas[i];
                let ggpoo = $vue.horarioGpo[kkeyy];
                if (ggpoo !== undefined) {
                    horasGpo.push($vue.horas[i]);
                }
            }
//            for (var i = 0; i < $vue.horarioGpo.length; i++) {
//                if ($vue.horarioGpo[i].dia.id == dia.id) {
//                    obj.push($vue.horarioGpo[i]);
//                }
//            }

            if (horasGpo.length === 0) {
                res = true;
            }

            var temp = parseInt(hora.numero);
            for (var i = 0; i < horasGpo.length; i++) {
                var horaSigui = parseInt(horasGpo[i].codigo) + 1;
                var horaAntes = parseInt(horasGpo[i].codigo) - 1;
                if (temp == horAnt || temp == horNext) {
                    res = true;
                }
            }

            return res;
        },
        clonarCiclo() {
            let $vue = this;
            $vue.ciclo = {id: null};
            $vue.$refs.modalCloneCiclo.open();
        },
        saveCloneCiclo() {
            let $vue = this;

            axios.post('/' + rutaModulo + '/clonarGrupos', $vue.ciclo).then(response => {
                if (response.data.success) {
                    $vue.$refs.modalCloneCiclo.close();
                    $vue.$refs.raptorGrupo.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }
            }).catch(function (error) {
                notify(Messages.errorComunicacion, "error");
            });

        },
        verEditarGrupo(item) {
            let $vue = this;
            $vue.grupoHorasForm = Object.assign({}, item);
            $vue.configEditGrupo.verPaleta = false;
            $vue.configEditGrupo.verLetras = false;
            $vue.grupoHorasForm.conHorarioEnum = {};
            for (var i = 0; i < $vue.tipoHorarios.length; i++) {
                if ($vue.grupoHorasForm.conHorario == $vue.tipoHorarios[i].name) {
                    $vue.grupoHorasForm.conHorarioEnum = $vue.tipoHorarios[i];
                }
            }
            $vue.$refs.modalEditGrupo.open();
        },
        saveEditGrupo() {
            let $vue = this;
            let form = $("#" + $vue.configEditGrupo.form);
            if (!form.parsley().validate()) {
                return;
            }

            $vue.grupoHorasForm.conHorario = $vue.grupoHorasForm.conHorarioEnum.name;
            $vue.$refs.modalEditGrupo.beginProcessing();
            axios.post('/' + rutaModulo + '/save', $vue.grupoHorasForm).then(response => {
                $vue.$refs.modalEditGrupo.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.verHorario($vue.grupoActivo);
                    $vue.$refs.raptorGrupo.loadRemoteData();
                    notify(response.data.message, "success");
                } else {
                    notify(response.data.message, "error");
                }

            }).catch(function (error) {
                $vue.$refs.modalEditGrupo.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });
        },
        verColores() {
            let $vue = this;
            $vue.configEditGrupo.verPaleta = $vue.configEditGrupo.verPaleta ? false : true;
            $vue.configEditGrupo.verLetras = false;
        },
        findBgColor(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = nn + 11 * (mm - 1) - 1;

            return "background-color:" + $vue.colors[idx] + ";";
        },
        setColor(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = nn + 11 * (mm - 1) - 1;
            $vue.grupoHorasForm.color = $vue.colors[idx];
        },
        getLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            return $vue.letras[idx];
        },
        setLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            $vue.grupoHorasForm.letra = $vue.letras[idx];
        },
        styleLetra(n, m) {
            let $vue = this;
            let nn = parseInt(n);
            let mm = parseInt(m);
            let idx = mm + 6 * (nn - 1) - 1;
            let letra = $vue.letras[idx];
            if ($vue.grupoHorasForm.letra == letra) {
                return "background-color: green; color: white;"
            }
            return "";

        },
        verAbc() {
            let $vue = this;
            $vue.configEditGrupo.verLetras = $vue.configEditGrupo.verLetras ? false : true;
            $vue.configEditGrupo.verPaleta = false;
        },
        revisar(tipo, ofi, campo) {
            let $vue = this;
            if (ofi[campo] == undefined) {
                return;
            }
            if (tipo == 'CODIGO') {
                ofi[campo] = VUE.revisarCodigo(ofi[campo]);
            } else if (tipo == 'EMAIL') {
                ofi[campo] = VUE.revisarEmail(ofi[campo]);
            } else if (tipo == 'NOMBRE') {
                ofi[campo] = VUE.revisarNombreObjeto(ofi[campo]);
            } else if (tipo == 'ANEXOS') {
                ofi[campo] = VUE.revisarAnexos(ofi[campo]);
            } else if (tipo == 'TELEFONOS') {
                ofi[campo] = VUE.revisarTelefonos(ofi[campo]);
            }
        },
        ocultar(item) {
            let $vue = this;

            $vue.configConfirmAction.message = '¿Está seguro que desea ocultar este grupo-horario de este ciclo?';
            $vue.configConfirmAction.okbtn = 'Si, ocultar grupo';
            $vue.configConfirmAction.okclass = "btn-warning";
            $vue.configConfirmAction.okaction = function () {
                axios.post(`/${rutaModulo}/ocultar`, {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        $vue.$refs.raptorGrupo.loadRemoteData();
                        notify(response.data.message, "info");
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(function (error) {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    console.log(error);
                    notify(Messages.errorComunicacion, "error");
                });
            };
            $vue.$refs.modalConfirmAction.open();
        },
        eliminar(item) {
            let $vue = this;

            $vue.configConfirmAction.message = '¿Está seguro que desea eliminar este grupo-horario?';
            $vue.configConfirmAction.okbtn = 'Si, eliminar grupo';
            $vue.configConfirmAction.okclass = "btn-danger";
            $vue.configConfirmAction.okaction = function () {
                axios.post(`/${rutaModulo}/delete`, {id: item.id}).then(response => {
                    $vue.$refs.modalConfirmAction.confirmReaction(response.data.success);
                    if (response.data.success) {
                        $vue.$refs.raptorGrupo.loadRemoteData();
                        notify(response.data.message, "info");
                    } else {
                        notify(response.data.message, "error");
                    }
                }).catch(function (error) {
                    $vue.$refs.modalConfirmAction.confirmReaction(false);
                    console.log(error);
                    notify(Messages.errorComunicacion, "error");
                });
            };
            $vue.$refs.modalConfirmAction.open();
        },
        nuevo() {
            let $vue = this;
            $vue.configEditGrupo.verPaleta = false;
            $vue.configEditGrupo.verLetras = false;
            $vue.grupoHorasForm = {
                codigo: '', letra: '', color: '', tipoSeccion: '', conHorario: '',
                tipoGrupoHoras: $vue.tipoGpo,
                conHorarioEnum: {name: '', value: ''}
            };
            for (var i = 0; i < $vue.tipoHorarios.length; i++) {
                if ($vue.grupoHorasForm.conHorario == $vue.tipoHorarios[i].name) {
                    $vue.grupoHorasForm.conHorarioEnum = $vue.tipoHorarios[i];
                }
            }
            $vue.$refs.modalEditGrupo.open();
        },
        verActivarGpos() {
            let $vue = this;
            $vue.ocultos = [];
            $vue.ocultosSelect = [];

            $vue.cfgActivarGrupo.waiting = true;
            $vue.cfgActivarGrupo.okbtn = "Activar grupos-horas";
            $vue.$refs.modalActivarGrupo.open();

            axios.post('/' + rutaModulo + '/listOcultos', $vue.tipoGpo).then(response => {
                $vue.cfgActivarGrupo.waiting = false;
                if (response.data.success) {
                    $vue.ocultos = response.data.data;
                } else {
                    notify(response.data.message, "error");
                }

            }).catch(function (error) {
                $vue.cfgActivarGrupo.waiting = false;
                notify(Messages.errorComunicacion, "error");
            });
        },
        selectGpoHide(item) {
            let $vue = this;
            let idx = -100;
            for (var i = 0; i < $vue.ocultosSelect.length; i++) {
                if (item.id == $vue.ocultosSelect[i].id) {
                    idx = i;
                }
            }
            if (idx === -100) {
                $vue.ocultosSelect.push(item);
            } else {
                $vue.ocultosSelect.splice(idx, 1);
            }
        },
        styleBorderGpoHide(item) {
            let $vue = this;

            for (var i = 0; i < $vue.ocultosSelect.length; i++) {
                if (item.id == $vue.ocultosSelect[i].id) {
                    return "background-color:gray;";
                }
            }
            if (item.color == undefined) {
                return "border-color:red;";
            }
            return "border-color:" + item.color + ";";
        },
        activarGpos() {
            let $vue = this;
            if ($vue.ocultosSelect.length === 0) {
                notify("Debe seleccionar que grupos-horas desea activar", "error");
                return;
            }

            axios.post('/' + rutaModulo + '/activarOcultos', $vue.ocultosSelect).then(response => {
                $vue.$refs.modalActivarGrupo.confirmReaction(response.data.success);
                if (response.data.success) {
                    $vue.$refs.raptorGrupo.loadRemoteData();
                    notify(response.data.message, "info");
                } else {
                    notify(response.data.message, "error");
                }

            }).catch(function (error) {
                $vue.$refs.modalActivarGrupo.confirmReaction(false);
                notify(Messages.errorComunicacion, "error");
            });

        }
    }
});
