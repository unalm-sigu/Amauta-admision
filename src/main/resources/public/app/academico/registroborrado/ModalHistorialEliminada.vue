<template>
  <modal-vik ref="modalHistorialBorrada"
             v-bind="modalHistorialBorrada">
    <div slot="body">

      <h4 class="text-primary block m-b-lg"> {{ title }} </h4>

      <form v-bind:id="form">
        <template v-if="infoHistorial">
          <div class="form-group">
            <table class="table table-hover">
              <thead>
              <tr>
                <th class="col-md-9">Curso</th>
                <th class="col-md-2">Nota</th>
                <th class="col-md-1 text-left">Estado</th>
              </tr>
              </thead>
              <tbody>
              <tr v-for="(item, index) in infoHistorial.alumnoCicloCursos">

                <td class="v-middle">
                  <span class="block text-primary">{{ item.curso.codigo }} {{ item.curso.nombre }}</span>
                  <span class="block"></span>
                </td>
                <td class="v-middle">
                  <span class="block text-primary">{{ item.nota }}</span>
                  <span class="block"></span>
                </td>
                <td class="v-middle">
                  <div v-bind:class="style(item.estado)">
                    {{ item.estadoEnum.value }}
                  </div>
                </td>

              </tr>
              </tbody>
            </table>
          </div>

        </template>

      </form>
    </div>
  </modal-vik>
</template>

<script>

module.exports = {

  data() {
    return {
      infoAlumno: {alumno: '', cicloAfectado: ''},
      infoHistorial: {alumnoCicloCursos: []},
      form: "id-form-histo-alumno",
      title: "Historial borrado",
      modalHistorialBorrada: VUE_MODAL.structInfo({
        id: 'id-modal-histo-alumno',
        cancelbtn: 'Cerrar',
        cancelclass: 'btn btn-link',
        showaccept: true
      })
    };
  },
  methods: {
    open(infoAlumno) {
      var form = $("#" + this.form);
      form.parsley().destroy();

      this.getInfoAcademico(infoAlumno);
      this.$refs.modalHistorialBorrada.open();
    },
    getInfoAcademico(infoAlumno) {
      myUtils.axios(VUE_AXIOS.structGetData({
        url: `/academico/registroborradoalu/getInfoacademicoEliminado`,
        body: infoAlumno
      })).then((resp) => this.infoHistorial = resp.data.data || {alumnoCicloCursos: []});

    },
    style(item) {
      var colorEstado = {MAT: 'success', PMAT: 'warning', NMAT: 'default'};
      var res = colorEstado[item];
      if (res === undefined) {
        return "label label-danger";
      }
      return "label label-" + res;
    },

    // metodos genericos
    getObjectId: myUtils.getObjectId,
    getObjectName: myUtils.getObjectName,
    commas: myUtils.commas
  }
};
</script>