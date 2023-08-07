<template>
    <div>
        <header class="header b-b padder-lg">
            <div class="pull-right m-t-md">
                <a v-bind:href="origen" class="btn btn-default">Regresar</a>
            </div>
            <h2>Información Académica</h2>

            <header-info v-bind:alumno="alumno"></header-info>

            <div class="tabbable-line">
                <ul class="nav nav-tabs" >
                    <li v-for="tab in tabs" v-bind:class="styleMenu(tab.id)">
                        <a href="#" data-toggle="tab" v-on:click="updateTabs(tab)" v-text="tab.name"></a>
                    </li>
                </ul>
            </div>

        </header>

        <section class="wrapper-lg">
            <section class="panel m-b-md">
                <section class="panel-body">

                    <div v-show="1 == tabId"  >
                        <inicio-info v-bind:alumno="alumno"></inicio-info>
                    </div>
                    <div v-show="2 == tabId" >
                        <histo-alumno v-bind:alumno="alumno" ref="histoAlumno" v-bind:showactions="true"></histo-alumno>
                    </div>
                    <div v-show="3 == tabId" >
                        <avance-alumno v-bind:alumno.sync="alumno" ref="avanceAlumno"></avance-alumno>
                    </div>
                    <div v-show="4 == tabId" >
                        <matricula-alumno v-bind:alumno="alumno" ref="matriculaAlumno"></matricula-alumno>
                    </div>
                    <div v-show="5 == tabId"  >
                        <horario-alumno v-bind:alumno="alumno" v-bind:ciclo="ciclo" ref="horarioAlumno"></horario-alumno>
                    </div>
                    <div v-show="6 == tabId">
                        <malla-alumno v-bind:alumno="alumno" ref="mallaAlumno"></malla-alumno>
                    </div>
                    <div v-show="8 == tabId">
                        <retiro-ciclo-alumno v-bind:alumno="alumno" ref="retiroCicloAlumno"></retiro-ciclo-alumno>
                    </div>
                    <div v-show="9 == tabId">
                        <retiro-curso-alumno v-bind:alumno="alumno" ref="retiroCursoAlumno"></retiro-curso-alumno>
                    </div>
                    <div v-show="10 == tabId">
                        <admision-alumno v-bind:alumno="alumno" ref="admisionAlumno"></admision-alumno>
                    </div>

                </section>
            </section>
        </section>

        <modal-confirm ref="modalConfirm"></modal-confirm>
        <modal-info ref="modalInfo"></modal-info>
    </div>

</template>
<script>
    Vue.component("multiselect", window.VueMultiselect.default);
    Vue.component("date-picker", window.VueBootstrapDatetimePicker.default);

    const ModalConfirm = httpVueLoader('/app/_componentes/ModalConfirm.vue');
    const ModalInfo = httpVueLoader('/app/_componentes/ModalInfo.vue');
    const HeaderInfo = httpVueLoader('./componentes/HeaderInfo.vue');
    const InicioInfo = httpVueLoader('./componentes/InicioInfo.vue');
    const HistoAlumno = httpVueLoader('./componentes/HistoAlumno.vue');
    const AvanceAlumno = httpVueLoader('./componentes/AvanceAlumno.vue');
    const MatriculaAlumno = httpVueLoader('./componentes/MatriculaAlumno.vue');
    const HorarioAlumno = httpVueLoader('./componentes/HorarioAlumno.vue');
    const MallaAlumno = httpVueLoader('./componentes/MallaAlumno.vue');
    const RetiroCicloAlumno = httpVueLoader('./componentes/RetiroCicloAlumno.vue');
    const RetiroCursoAlumno = httpVueLoader('./componentes/RetiroCursoAlumno.vue');
    const AdmisionAlumno = httpVueLoader('./componentes/AdmisionAlumno.vue');

    module.exports = {
        components: {
            ModalConfirm, ModalInfo, HeaderInfo, InicioInfo, HistoAlumno,
            AvanceAlumno, MatriculaAlumno, HorarioAlumno, MallaAlumno,
            RetiroCicloAlumno, RetiroCursoAlumno, AdmisionAlumno
        },
        data() {
            return {
                origen: origen,
                alumno: JSON.parse(alumnoJson),
                ciclo: JSON.parse(cicloJson),
                tabId: 1,
                loadPages: {
                    historial: false,
                    avance: false,
                    matricula: false,
                    horario: false,
                    malla: false,
                    aportes: false,
                    retirociclo: false,
                    retirocurso: false,
                    admision: false
                },
                tabs: [
                    {id: 1, name: "Inicio"},
                    {id: 2, name: "Historial"},
                    {id: 3, name: "Avance"},
                    {id: 4, name: "Matricula"},
                    {id: 5, name: "Horario"},
                    {id: 6, name: "Malla"},
                    {id: 8, name: "Retiro Ciclo"},
                    {id: 9, name: "Retiro Curso"}
                ]
            };
        },
        mounted() {
            if (this.alumno.modalidadEstudio.codigo == 'PRE') {
                this.tabs.push({id: 10, name: "Admisión y Nivelación"});
            }
        },
        computed: {
        },
        methods: {
            updateTabs(tab) {
                let $vue = this;
                this.tabId = tab.id;
                if (this.tabId === 2 && !this.loadPages.historial) {
                    this.$refs.histoAlumno.cargaHistorial();
                    this.loadPages.historial = true;
                }
                if (this.tabId === 3 && !this.loadPages.avance) {
                    this.$refs.avanceAlumno.loadPlanes();
                    this.$refs.avanceAlumno.cargaAvance();
                    this.loadPages.avance = true;
                }
                if (this.tabId === 4 && !this.loadPages.matricula) {
                    this.$refs.matriculaAlumno.obtenerDatos();
                    this.loadPages.matricula = true;
                }
                if (this.tabId === 5 && !this.loadPages.horario) {
                    this.$refs.horarioAlumno.cargaHorario();
                    this.loadPages.horario = true;
                }
                if (this.tabId === 6 && !this.loadPages.malla) {
                    this.$refs.mallaAlumno.verMalla();
                    this.loadPages.malla = true;
                }
                if (this.tabId === 7) {
                    //this.cargaAportes();
                }
                if (this.tabId === 8 && !this.loadPages.retirociclo) {
                    this.$refs.retiroCicloAlumno.obtenerDatos();
                    this.loadPages.retirociclo = true;
                }
                if (this.tabId === 9 && !this.loadPages.retirocurso) {
                    this.$refs.retiroCursoAlumno.obtenerDatos();
                    this.loadPages.retirocurso = true;
                }
                if (this.tabId === 10 && !this.loadPages.admision) {
                    this.$refs.admisionAlumno.obtenerDatos();
                    this.loadPages.admision = true;
                }
            },
            styleMenu(index) {
                let id = this.tabId;
                if (index == id) {
                    return "active";
                }
            },
            reiniciarPlanes() {
                this.reloadAlumno();
                this.loadPages.avance = false;
                this.loadPages.malla = false;
            },
            reloadPlanAlumno() {
                this.reloadAlumno();
                this.loadPages.malla = false;
            },
            reloadAlumno() {
                myUtils.axios(VUE_AXIOS.structGetData({
                    url: `/${rutaModulo}/${this.alumno.id}/data`
                })).then((resp) => this.alumno = resp.data.data);
            },

            // metodos genericos
            getOrigenURL: myUtils.getOrigenURL,
            activarNumeric: myUtils.activarNumeric,
            getObjectId: myUtils.getObjectId,
            getObjectName: myUtils.getObjectName,
            commas: myUtils.commas
        }
    };

</script>