package com.vehicle.demo.controller;

import javax.servlet.http.HttpServletRequest;

import com.vehicle.demo.model.Vehicle;
import com.vehicle.demo.model.dto.VehicleRequest;
import com.vehicle.demo.model.dto.VehicleResponse;
import com.vehicle.demo.service.IVehicleService;
import com.vehicle.demo.util.paginator.PageRender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;


@Controller
@SessionAttributes("vehicle")
public class ViewController {
	
	@Autowired
	private IVehicleService iVehicleService;

	@RequestMapping(value = {"/listar", "/"}, method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
			HttpServletRequest request) {
				
		Pageable pageRequest = PageRequest.of(page, 4);

		Page<VehicleResponse> vehicles = iVehicleService.findAllList(pageRequest);

		PageRender<VehicleResponse> pageRender = new PageRender<VehicleResponse>("/listar", vehicles);
		
		model.addAttribute("titulo", "Listado");
		model.addAttribute("vehicles", vehicles);
		model.addAttribute("page", pageRender);
		
		return "listar";
		
	}

	@RequestMapping(value = "/filtro", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model,
						 @RequestParam(name = "searchBrand", required = false) String searchBrand,
						 @RequestParam(name = "searchModel", required = false) String searchModel,
						 @RequestParam(name = "searchLicense", required = false) String searchLicense,
						 HttpServletRequest request) {

		Pageable pageRequest = PageRequest.of(page, 4);

		Page<VehicleResponse> vehicles;

		searchBrand = searchBrand.isEmpty() ? null : searchBrand;
		searchModel = searchModel.isEmpty() ? null : searchModel;
		searchLicense = searchLicense.isEmpty() ? null : searchLicense;

		vehicles = iVehicleService.findList(searchBrand, searchModel, searchLicense, pageRequest);

		PageRender<VehicleResponse> pageRender = new PageRender<VehicleResponse>("/listar", vehicles);

		model.addAttribute("titulo", "Listado");
		model.addAttribute("vehicles", vehicles);
		model.addAttribute("page", pageRender);

		// Optional: Add search parameters to model for display
		model.addAttribute("searchBrand", searchBrand);
		model.addAttribute("searchModel", searchModel);
		model.addAttribute("searchLicense", searchLicense);

		return "listar";
	}

	@RequestMapping(value = "/form")
	public String crear(Map<String, Object> model, Locale locale) {

		Vehicle vehicle = new Vehicle();

		model.put("vehicle", vehicle);
		model.put("titulo", "Agregar Vehiculo");

		return "form";
	}

	@PostMapping("/form")
	public String guardar(VehicleRequest vehicleRequest, BindingResult result, Model model,
						  RedirectAttributes flash,
						  SessionStatus status, Locale locale) {

		if (result.hasErrors()) {
			model.addAttribute("titulo", "Agregar Vehiculo");
			return "/form";
		}

		iVehicleService.save(vehicleRequest);
		status.setComplete();

		flash.addFlashAttribute("success", "Registro éxitoso");


		return "redirect:/listar";
	}

	@RequestMapping(value = "/delete/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {

		Vehicle vehicle = iVehicleService.find(id);

		if (Objects.nonNull(vehicle)) {

			iVehicleService.delete(id);

			flash.addFlashAttribute("success", "Vehiculo borrado");

		}
		return "redirect:/listar";
	}

	@GetMapping("/form/{vehicleId}")
	public String crear(@PathVariable(value = "vehicleId") Long vehicleId, Map<String, Object> model,
						RedirectAttributes flash, Locale locale) {

		Vehicle vehicle = iVehicleService.find(vehicleId);

		if (vehicle == null) {
			flash.addFlashAttribute("error", "El vehiculo no existe en la base de datos");
			return "redirect:/listar";
		}

		model.put("vehicle", vehicle);
		model.put("titulo", "Modificar Vehiculo");

		return "/form";
	}
	
}
