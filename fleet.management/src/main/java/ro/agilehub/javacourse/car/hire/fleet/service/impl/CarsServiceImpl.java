package ro.agilehub.javacourse.car.hire.fleet.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ro.agilehub.javacourse.car.hire.api.common.PatchMapper;
import ro.agilehub.javacourse.car.hire.api.model.CarRequestDTO;
import ro.agilehub.javacourse.car.hire.api.model.CarResponseDTO;
import ro.agilehub.javacourse.car.hire.api.model.PageCars;
import ro.agilehub.javacourse.car.hire.api.model.PatchDocument;
import ro.agilehub.javacourse.car.hire.fleet.document.CarDoc;
import ro.agilehub.javacourse.car.hire.fleet.mapper.CarMapper;
import ro.agilehub.javacourse.car.hire.fleet.repository.CarRepository;
import ro.agilehub.javacourse.car.hire.fleet.service.CarsService;
import ro.agilehub.javacourse.car.hire.patch.repository.DocumentPatchRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CarsServiceImpl implements CarsService {

    private final CarRepository carRepository;
    private final CarMapper carMapper;
    private final DocumentPatchRepository<CarDoc, String> patchRepository;

    @Override
    public String addCar(CarRequestDTO carDTO) {
        CarDoc carDoc = carMapper.mapToCarDoc(carDTO);
        try {
            carDoc = carRepository.save(carDoc);
            return carDoc.get_id();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean deleteCar(String id) {
        CarDoc car = getCarDoc(id);
        try {
            carRepository.delete(car);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public CarDoc getCarDoc(String id) {
        return carRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("No car found with id " + id));
    }

    @Override
    public CarResponseDTO getCar(String id) {
        CarDoc carDoc = getCarDoc(id);
        return carMapper.mapToCarResponseDTO(carDoc);
    }

    @Override
    public PageCars findAll(Integer page, Integer size, String sort, String status) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CarDoc> pageCarsDoc = carRepository.findAllByStatus(status, pageable);
        PageCars pageCars = new PageCars();
        pageCars.currentPage(page)
                .pageSize(pageCarsDoc.getNumberOfElements())
                .totalNoRecords((int) pageCarsDoc.getTotalElements())
                .totalPages(pageCarsDoc.getTotalPages());
        if (pageCarsDoc.hasContent())
            pageCarsDoc.forEach(car -> pageCars.addCarsItem(
                    carMapper.mapToCarResponseDTO(car)));
        return pageCars;
    }

    @Override
    public boolean updateCar(String id, List<PatchDocument> patchDocuments) {
        PatchMapper patchMapper = PatchMapper.getPatchMapper(patchDocuments, CarDoc.class);
        return patchRepository.updateDoc(CarDoc.class, id, patchMapper.getFieldValues());
    }

}
