Vue.component("horario-component", {
    template: "#horarioComponent",
    props: {
        alumno: {},
        ciclo: {},
        docente: {},
        tipo: {
            required: false,
            type: String,
            default: "ALU"
        }
    },
    data: function () {
        return {
            titulo: "Nada",
            tabla4: false,
            tabla8: false,
            tabla14: false,
            horarios: [],
            secciones: [],
            coloresCurso: [],
            horas: [],
            allHoras: JSON.parse(horasJson),
        }
    },
    computed: {
    },
    beforeMount() {
    },
    mounted() {
        let $vue = this;
        $vue.titulo = "Horario de Clases " + $vue.ciclo.descripcion;
    },
    methods: {
        cargaHorario() {
            let $vue = this;
            let id = null;
            if ($vue.tipo == 'ALU') {
                id = $vue.alumno.id;
            } else if ($vue.tipo == 'DOC') {
                id = $vue.docente.id;
            }
            $.ajax({
                method: 'GET',
                url: APP.url('academico/alumno/' + id + '/horario'),
                data: {tipo: $vue.tipo},
                contentType: "application/json",
                success: function (response) {
                    $vue.horarios = response.data;
                    $vue.settingSeccionColor();
                    $vue.settingHoras();
                    $vue.validandoTabla();
                }
            });
        },
        settingSeccionColor: function () {
            let vue = this;
            var horarios = vue.horarios.horarios;
            for (var i = 0; i < horarios.length; i++) {
                for (var j = 0; j < horarios[i].dias.length; j++) {
                    for (var m = 0; m < horarios[i].dias[j].secciones.length; m++) {
                        if (vue.secciones.indexOf(horarios[i].dias[j].secciones[m].codigo2) === -1) {
                            let seccion = horarios[i].dias[j].secciones[m].codigo2;
                            vue.secciones.push(seccion);
                        }
                    }
                }
            }

            vue.secciones.map(function (data, index) {
                let seccionColor = {
                    seccion: data,
                    color: 'color' + (index + 1)
                }
                vue.coloresCurso.push(seccionColor);
            });
        },
        getColorBySeccion(seccion) {
            let vue = this;
            let classDiv = "";
            vue.coloresCurso.map(function (data, index) {
                if (seccion.codigo2 === data.seccion) {
                    classDiv = data.color;
                }
            });
            if (seccion.horasContinuas == 1) {
                return  "curso size-1 " + classDiv;
            } else {
                return "curso size-" + seccion.horasContinuas + "-horas " + classDiv;
            }
        },
        settingHoras() {
            let vue = this;
            var horarios = vue.horarios.horarios;
            for (var i = 0; i < horarios.length; i++) {
                if (vue.horas.indexOf(horarios[i].numeroHora) == -1) {
                    let numeroHora = horarios[i].numeroHora;
                    vue.horas.push(numeroHora);
                }
            }

        },
        validandoTabla() {
            let vue = this;
            let cantHoras = vue.horas.length;
            let horasTotal = []; //horas desde las 8 am
            vue.allHoras.map(function (data, index) {
                if (data.numero != 6 && data.numero != 7) {   //  menos 6 y 7
                    horasTotal.push(data.numero);
                }
            });
            //hora minima y maxima  del alumno
            var horaMin = Math.min.apply(null, horasTotal);
            var horaMax = Math.max.apply(null, horasTotal);
            //var longitudHoras = horasTotal.length;
            var posicionMayor = horasTotal.indexOf(horaMax);
            //eliminando las horas despues del mayor
            for (var i = horasTotal.length - 1; i >= 0; i--) {
                if (horasTotal[i] > posicionMayor) {
                    horasTotal.splice(horasTotal[i], 1);
                }
            }

            //obtener index
            let indexs = [];
            horasTotal.map(function (data, index) {
                indexs.push(horasTotal.indexOf(vue.horas[index]));
            });
            let horasRestante = [];
            //eliminando horas entre los rangos de horas minimo y maximo
            for (var i = horasTotal.length - 1; i >= 0; i--) {
                if (indexs[i] > -1) {
                    horasTotal.splice(indexs[i], 1);
                }
            }

            horasRestante = horasTotal;
            if (cantHoras <= 4) {
                let horasLlenar = 4 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                vue.tabla4 = true;
            }
            if (cantHoras >= 5 && cantHoras <= 8) {
                let horasLlenar = 8 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                vue.tabla8 = true;
            }
            if (cantHoras >= 9) {
                let horasLlenar = 15 - cantHoras;
                if (horasLlenar == 1) {
                    vue.llenarSeccion(vue, 1, horasRestante);
                }
                if (horasLlenar == 2) {
                    vue.llenarSeccion(vue, 2, horasRestante);
                }
                if (horasLlenar == 3) {
                    vue.llenarSeccion(vue, 3, horasRestante);
                }
                if (horasLlenar == 4) {
                    vue.llenarSeccion(vue, 4, horasRestante);
                }
                if (horasLlenar == 5) {
                    vue.llenarSeccion(vue, 5, horasRestante);
                }
                if (horasLlenar == 6) {
                    vue.llenarSeccion(vue, 6, horasRestante);
                }
                vue.tabla14 = true;
            }

        },
        llenarSeccion(vue, index, horasRestante) {
            for (var i = 0; i < index; i++) {
                let horaAdd = horasRestante[i];
                if (horaAdd !== undefined) {
                    let dias = [];
                    vue.getDescripcionByNroHora(horaAdd);
                    vue.getDiasHorasVacias(dias, vue.horaTmp);
                    let itemAdd = {dias: dias, hora: vue.horaTmp, numeroHora: horaAdd};
                    vue.horarios.horarios.push(itemAdd)
                    vue.horarios.horarios.sort(function (a, b) {
                        if (a.numeroHora < b.numeroHora) {
                            return -1;
                        }
                        if (a.numeroHora > b.numeroHora) {
                            return 1;
                        }
                        return 0
                    });
                }
            }
        },
        getDiasHorasVacias(dias, hora) {
            let lunes = {dia: "Lunes", hora: hora, secciones: []};
            let martes = {dia: "Martes", hora: hora, secciones: []};
            let miercoles = {dia: "Miercoles", hora: hora, secciones: []};
            let jueves = {dia: "Jueves", hora: hora, secciones: []};
            let viernes = {dia: "Viernes", hora: hora, secciones: []};
            let sabado = {dia: "Sabado", hora: hora, secciones: []};
            let domingo = {dia: "Domingo", hora: hora, secciones: []};
            dias.push(lunes);
            dias.push(martes);
            dias.push(miercoles);
            dias.push(jueves);
            dias.push(viernes);
            dias.push(sabado);
            dias.push(domingo);
        },
        getDescripcionByNroHora(numero) {
            let vue = this;
            $.ajax({
                async: false,
                method: 'GET',
                url: APP.url('academico/alumno/' + numero + '/hora'),
                contentType: "application/json",
                success: function (response) {
                    vue.horaTmp = response.data.descripcion;
                }
            });
        },
        getHeigth(seccion) {
            return "";
            if (seccion.horasContinuas == 1) {
                return "";
            }

            let sty = "min-height: calc(" + seccion.horasContinuas + "*100% - 4px); ";
            sty += "max-height: calc(" + seccion.horasContinuas + "*100% - 4px); ";
            return sty;

//            let $vue = this;
//            if ($vue.tabla14) {
//                return "min-height: calc(200% - 4px); " + (seccion.horasContinuas * 28) + "px;";
//            } else {
//                return "min-height: " + (seccion.horasContinuas * 54) + "px;";
//            }
        },
        verificarHora(dia) {
            let secciones = dia.secciones;
            if (secciones.length == 0) {
                return true;
            }
            let seccion = secciones[0];
            return seccion.horaInicial;
        }
    }
});