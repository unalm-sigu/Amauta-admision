<template>
  <div>
    <header class="header b-b padder-lg">
      <div class="pull-right m-t-md">
        <a v-bind:href="origen" class="btn btn-default">Regresar</a>
      </div>
      <h2>Información Académica</h2>

      <header-info v-bind:alumno="alumno"></header-info>

      <div class="tabbable-line">
        <ul class="nav nav-tabs">
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
          <div v-show="2 == tabId">
            <histo-alumno v-bind:alumno="alumno" ref="histoAlumno" v-bind:showactions="true"></histo-alumno>
          </div>
          <div v-show="4 == tabId">
            <matricula-alumno v-bind:alumno="alumno" ref="matriculaAlumno"></matricula-alumno>
          </div>
          <div v-show="8 == tabId">
            <retiro-ciclo-alumno v-bind:alumno="alumno" ref="retiroCicloAlumno"></retiro-ciclo-alumno>
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
const HeaderInfo = httpVueLoader('/app/academico/alumno/componentes/HeaderInfo.vue');
const InicioInfo = httpVueLoader('/app/academico/alumno/componentes/InicioInfo.vue');
const HistoAlumno = httpVueLoader('/app/academico/alumno/componentes/HistoAlumno.vue');
const MatriculaAlumno = httpVueLoader('/app/academico/alumno/componentes/MatriculaAlumno.vue');
const RetiroCicloAlumno = httpVueLoader('/app/academico/alumno/componentes/RetiroCicloAlumno.vue');

module.exports = {
  components: {
    ModalConfirm, ModalInfo, HeaderInfo,InicioInfo,
    HistoAlumno, MatriculaAlumno, RetiroCicloAlumno
  },
  data() {
    return {
      origen: origen,
      alumno: JSON.parse(alumnoJson),
      ciclo: JSON.parse(cicloJson),
      tabId: 1,
      loadPages: {
        historial: false,
        matricula: false,
        retirociclo: false
      },
      tabs: [
        {id: 1, name: "Inicio"},
        {id: 2, name: "Historial"},
        {id: 4, name: "Matricula"},
        {id: 8, name: "Retiro Ciclo"}
      ]
    };
  },
  mounted() {
    this.updateTabs({ id: this.tabId });
  },
  methods: {
    updateTabs(tab) {
      this.tabId = tab.id;
      if (this.tabId === 2 && !this.loadPages.historial) {
        this.$refs.histoAlumno.cargaHistorial();
        this.loadPages.historial = true;
      }
      if (this.tabId === 4 && !this.loadPages.matricula) {
        this.$refs.matriculaAlumno.obtenerDatos();
        this.loadPages.matricula = true;
      }
      if (this.tabId === 8 && !this.loadPages.retirociclo) {
        this.$refs.retiroCicloAlumno.obtenerDatos();
        this.loadPages.retirociclo = true;
      }
    },
    styleMenu(index) {
      return index === this.tabId ? "active" : "";
    },
    reloadAlumno() {
      myUtils.axios(VUE_AXIOS.structGetData({
        url: `/${rutaModulo}/${this.alumno.id}/data`
      })).then((resp) => this.alumno = resp.data.data);
    },

    // métodos utilitarios
    getOrigenURL: myUtils.getOrigenURL,
    activarNumeric: myUtils.activarNumeric,
    getObjectId: myUtils.getObjectId,
    getObjectName: myUtils.getObjectName,
    commas: myUtils.commas
  }
};
</script>
