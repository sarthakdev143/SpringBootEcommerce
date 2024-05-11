package com.example.web_app.service;

import com.example.web_app.domain.Grain;
import com.example.web_app.domain.Order;
import com.example.web_app.domain.User;
import com.example.web_app.model.GrainDTO;
import com.example.web_app.repos.GrainRepository;
import com.example.web_app.repos.OrderRepository;
import com.example.web_app.repos.UserRepository;
import com.example.web_app.util.NotFoundException;
import com.example.web_app.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class GrainService {

    private final GrainRepository grainRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    public GrainService(final GrainRepository grainRepository, final UserRepository userRepository,
            final OrderRepository orderRepository) {
        this.grainRepository = grainRepository;
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
    }

    public List<GrainDTO> findAll() {
        final List<Grain> grains = grainRepository.findAll(Sort.by("grainId"));
        return grains.stream()
                .map(grain -> mapToDTO(grain, new GrainDTO()))
                .toList();
    }

    public GrainDTO get(final Integer grainId) {
        return grainRepository.findById(grainId)
                .map(grain -> mapToDTO(grain, new GrainDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final GrainDTO grainDTO) {
        final Grain grain = new Grain();
        mapToEntity(grainDTO, grain);
        return grainRepository.save(grain).getGrainId();
    }

    public void update(final Integer grainId, final GrainDTO grainDTO) {
        final Grain grain = grainRepository.findById(grainId)
                .orElseThrow(NotFoundException::new);
        mapToEntity(grainDTO, grain);
        grainRepository.save(grain);
    }

    public void delete(final Integer grainId) {
        grainRepository.deleteById(grainId);
    }

    public GrainDTO mapToDTO(final Grain grain, final GrainDTO grainDTO) {
        grainDTO.setGrainId(grain.getGrainId());
        grainDTO.setGrainName(grain.getGrainName());
        grainDTO.setDescription(grain.getDescription());
        grainDTO.setPrice(grain.getPrice());
        grainDTO.setQuantity(grain.getQuantity());
        grainDTO.setSeller(grain.getSeller() == null ? null : grain.getSeller().getUserId());
        return grainDTO;
    }

    public Grain mapToEntity(final GrainDTO grainDTO, final Grain grain) {
        grain.setGrainName(grainDTO.getGrainName());
        grain.setDescription(grainDTO.getDescription());
        grain.setPrice(grainDTO.getPrice());
        grain.setQuantity(grainDTO.getQuantity());
        final User seller = grainDTO.getSeller() == null ? null
                : userRepository.findById(grainDTO.getSeller())
                        .orElseThrow(() -> new NotFoundException("seller not found"));
        grain.setSeller(seller);
        return grain;
    }

    public boolean grainNameExists(final String grainName) {
        return grainRepository.existsByGrainNameIgnoreCase(grainName);
    }

    public ReferencedWarning getReferencedWarning(final Integer grainId) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Grain grain = grainRepository.findById(grainId)
                .orElseThrow(NotFoundException::new);
        final Order grainOrder = orderRepository.findFirstByGrain(grain);
        if (grainOrder != null) {
            referencedWarning.setKey("grain.order.grain.referenced");
            referencedWarning.addParam(grainOrder.getOrderId());
            return referencedWarning;
        }
        return null;
    }

}
