var app = new Vue({
    el: '#main',
    data: {
        URL: APP.url('academico/asignacionaula'),
        processing: false,
        asignacionAula: null,
        progresoAsignacionModal: {
            id: 'progresoAsignacionModal',
            header: true,
            title: 'Progreso de asignación de aulas',
            footer: false,
            btnclose: false,
            modalsize: 'modal-md',
            dataKeyboard : 'false',
            dataBackdrop : 'static'
        }
    }, created: function () {
        if (jAsignacionAula != null && jAsignacionAula != '') {
            this.asignacionAula = JSON.parse(jAsignacionAula);
        }

    }, mounted: function () {
        let $vue = this;

    }, methods: {
        getEstadoClass(estado) {
            return "label " + APP.getEstadoClass(estado);
        },
        procesarAsignacionAulas() {
            let vue = this;
//            MODAL.showWait("Espere un momento por favor");
            /*if (vue.asignacionAula == null) {
                vue.asignacionAula = {id: ""};
            }*/
            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la asignación de aulas?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        vue.ejecutarAsignacionParcial();
                    } else {
                        //MODAL.hideWait();
                    }
                }
            });

        }, 
        eliminarAsignacion() {
            let vue = this;
//            MODAL.showWait("Espere un momento por favor");

            bootbox.confirm({
                message: "¿Está seguro que desea eliminar la asignación de aulas?",
                buttons: {
                    confirm: {label: 'Si', className: "btn-warning btn-modal btn-procesar"},
                    cancel: {label: 'Cancelar', className: "btn-link btn-modal"}
                },
                callback: function (result) {
                    if (result) {
                        MODAL.showWait("Espere un momento por favor");
                        AXIOS.post(`${vue.URL}/aliminarAsignacion`, vue.asignacionAula)
                            .then(response => {
                                if (response.data.success) {
                                    vue.asignacionAula = null;
                                    //  vue.asignacionAula = response.data.data;
                                    // vue.loadAsignacionAula();
                                    MODAL.hideWait();
                                } else {
                                    notify(response.data.message, 'error');
                                    MODAL.hideWait();
                                }
                            }).catch(function (error) {                                
                                notify(Messages.errorComunicacion, "error");
                                MODAL.hideWait();
                            });
                    } else {
                        //MODAL.hideWait();
                    }
                }
            });
        }, 
        loadAsignacionAula() {
            let vue = this;
            AXIOS.post(`${this.URL}/loadAsignacionAula`, vue.asignacionAula)
                .then(response => {
                    if (response.data.success) {
                        vue.asignacionAula = response.data.data;
                    } else {
                        notify(response.data.message, 'error');
                    }
                }).catch(function (error) {
                    notify(Messages.errorComunicacion, "error");
                });
        },
        editarGpoSecciones(item) {
            console.dir(item);
            let $vue = this;
            let lista = item.idsGpoSecciones;
            if (lista == "") {
                return;
            }
            console.dir(lista);
            let listaEncode = Base64.encode(lista);
            let first = lista.split(",")[0];
            location.href = APP.url("academico/gposeccion/" + first + "/editar") + $vue.getOrigenURL() + "&ids=" + listaEncode;
        },
        getOrigenURL() {
            var url = window.location.href;
            console.log(url)
            return "?origen=" + Base64.encode(url);
        },
        chunk(arr, size){
            let result = arr.reduce((rows, key, index) => (index % size == 0 ? rows.push([key]) : rows[rows.length-1].push(key)) && rows, []);
            return result;
        },
        modificarPorcentaje(porcentaje){
            $(".progress-bar").css('width', porcentaje + "%");
            $(".progress-bar").attr('aria-valuenow',porcentaje);
            $(".progress-bar").text(porcentaje + "%");
        },
        modificarContadorSecciones(ingresados,total){
            $(".progess_seccion").text(" " + ingresados + " / " + total);
        },
        async ejecutarAsignacionParcial(){
            let vue = this;
            //await AXIOS.post(`${vue.URL}/aliminarAsignacion`, vue.asignacionAula);
            try {        
                //vue.asignacionAula = vue.asignacionAula == null ? {id: ""} : vue.asignacionAula ;
                vue.$refs.progresoAsignacion.open();
                vue.modificarPorcentaje(0);
                vue.modificarContadorSecciones(0,0);
                const responseSeccionesForAsignacionAula = await AXIOS.get(`${vue.URL}/findSeccionesForAsignacionAula`);
                if (responseSeccionesForAsignacionAula.data.success) {
                    let seccionesArrayChunk = vue.chunk(responseSeccionesForAsignacionAula.data.data.secciones, 50);
                    const totalBloques = seccionesArrayChunk.length;
                    let bloqueAsignado = 0;
                    let cantidadSeccionesGuardados = 0;
                    for (let seccionesArrayIndex in seccionesArrayChunk) {
                    //for (let seccionesArraySSSSIndex in seccionesArrayChunk) {
                        vue.formmSecciones = {};
                        vue.formmSecciones.secciones = seccionesArrayChunk[seccionesArrayIndex];
                        const responseParcial = await axios_.post(`${vue.URL}/ejecutarAsigacionParcial`,vue.formmSecciones.secciones);
                        if(responseParcial.data.success){
                            bloqueAsignado++;
                            cantidadSeccionesGuardados = cantidadSeccionesGuardados +seccionesArrayChunk[seccionesArrayIndex].length;
                            let porcentaje = parseFloat((100*bloqueAsignado)/totalBloques).toFixed(2);
                            vue.modificarPorcentaje(porcentaje);
                            vue.modificarContadorSecciones(cantidadSeccionesGuardados,responseSeccionesForAsignacionAula.data.data.secciones.length)
                        }else{
                            throw new Error("Whoops!");
                        }
                    }
                    if(bloqueAsignado == totalBloques && responseSeccionesForAsignacionAula.data.data.secciones.length > 0){
                        vue.formAsignacionAula = {};
                        vue.formAsignacionAula.asignacionAula = vue.asignacionAula;
                        vue.formAsignacionAula.seccionesProgramadas = responseSeccionesForAsignacionAula.data.data.seccionesProgramadas;
                        vue.formAsignacionAula.seccionesAsignadas = responseSeccionesForAsignacionAula.data.data.seccionesAsignadas;
                        vue.formAsignacionAula.seccionesTipoAul = responseSeccionesForAsignacionAula.data.data.seccionesTipoAul;
                        vue.formAsignacionAula.seccionesTipoLab = responseSeccionesForAsignacionAula.data.data.seccionesTipoLab;
                        const responseAsignacionAula = await AXIOS.post(`${vue.URL}/saveAsignacionAula`,vue.formAsignacionAula);
                        if (responseAsignacionAula.data.success) {
                            //console.log(responseAsignacionAula.data)
                            vue.asignacionAula = responseAsignacionAula.data.data;
                        } else {
                            await axios_.post(`${vue.URL}/aliminarAsignacion`, vue.asignacionAula);
                            notify(responseAsignacionAula.data.message, 'error');
                        }
                    }                    
                    vue.$refs.progresoAsignacion.close();
                }else{
                    notify(responseSeccionesForAsignacionAula.data.message, 'error');
                    vue.$refs.progresoAsignacion.close();
                }
            } catch (e) {
                //vue.modificarPorcentaje(0);
                //vue.modificarContadorSecciones(0,0);
                vue.$refs.progresoAsignacion.close(); 
                await axios_.post(`${vue.URL}/aliminarAsignacion`, vue.asignacionAula);
                vue.asignacionAula = null;
                notify(Messages.errorComunicacion, 'error');
            }
        }
    }
})
