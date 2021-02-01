package com.akipav2.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.akipav2.dao.PlatosDAO;
import com.akipav2.entitys.Platos;
import com.akipav2.responses.ListaPlatosResponse;
import com.akipav2.responses.PlatoRegistroResponse;
import com.akipav2.responses.PlatoResponse;

@RestController
@RequestMapping("/platos")
public class PlatosREST {

	@Autowired
	private PlatosDAO platoDAO;

	// solo lista los platos 'disponibles'
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<ListaPlatosResponse> getPlatos() {

		ListaPlatosResponse response = new ListaPlatosResponse();
		List<Platos> platos = platoDAO.findAllAvailablePlatos();

		response.setPlatos(platos);

		if (platos.isEmpty() || platos == null) {
			response.setStatus("00");
			response.setMensaje("No hay platos disponibles");
			return ResponseEntity.ok(response);
		}

		response.setStatus("01");
		response.setMensaje("Solicitud Exitosa");
		return ResponseEntity.ok(response);
	}

	// obtiene un plato por su ID
	@RequestMapping(value = "{platoId}", method = RequestMethod.GET)
	public ResponseEntity<PlatoResponse> getPlatosById(@PathVariable("platoId") Long platoId) {

		PlatoResponse response = new PlatoResponse();
		Optional<Platos> optionalPlato = platoDAO.findById(platoId);

		if (optionalPlato.isPresent()) {
			response.setPlato(optionalPlato.get());
		}

		if (response.getPlato() == null) {
			response.setStatus("00");
			response.setMensaje("Plato no encontrado");
			return ResponseEntity.ok(response);
		} else {
			response.setStatus("01");
			response.setMensaje("Solicitud Exitosa");
			return ResponseEntity.ok(response);
		}

	}

	@PostMapping()
	public ResponseEntity<PlatoRegistroResponse> createPlato(@RequestBody Platos plato) {

		PlatoRegistroResponse response = new PlatoRegistroResponse();

		// validaciones
		if (plato.getNombre() == null || plato.getNombre().isBlank()) {
			response.setError("El Nombre es obligatorio");
			return ResponseEntity.ok(response);
			// no he logrado validar que el precio sea númerico
		} else if (Double.valueOf(plato.getPrecio().toString()) == null
				|| !Double.valueOf(plato.getPrecio()).toString().matches("^\\d\\d*(\\.\\d+)?$")) {
			response.setError("Debe ingresar un número para el precio");
			return ResponseEntity.ok(response);
		} else if (plato.getEstado() == null
				|| plato.getEstado().intValue() > 1 
				|| plato.getEstado().intValue() < 0) {
			response.setError("El estado debe ser 0: no disponible | 1: disponible");
			return ResponseEntity.ok(response);
		}

		// si llega acá es que pasó todas las validaciones
		platoDAO.save(plato);
		response.setExito("Registro exitoso");

		return ResponseEntity.ok(response);
	}

	@DeleteMapping(value = "{platoId}")
	public ResponseEntity<Void> deletePlato(@PathVariable("platoId") Long platoId) {
		platoDAO.deleteById(platoId);
		return ResponseEntity.ok(null);

	}

	@PutMapping()
	public ResponseEntity<Platos> updatePlato(@RequestBody Platos plato) {
		Optional<Platos> optionalPlato = platoDAO.findById(plato.getId());
		if (optionalPlato.isPresent()) {

			Platos updatePlato = optionalPlato.get();

			updatePlato.setNombre(plato.getNombre());
			updatePlato.setPrecio(plato.getPrecio());
			updatePlato.setEstado(plato.getEstado());
			updatePlato.setImagen(plato.getImagen());
			updatePlato.setDescripcionPlato(plato.getDescripcionPlato());

			platoDAO.save(updatePlato);

			return ResponseEntity.ok(updatePlato);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

}