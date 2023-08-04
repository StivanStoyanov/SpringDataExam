package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.StarSeedDto;
import softuni.exam.models.entity.Star;
import softuni.exam.repository.ConstellationRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.StarService;
import softuni.exam.util.ValidationUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class StarServiceImpl implements StarService {
    private final static String FILE_PATH = "src/main/resources/files/json/stars.json";
    private final StarRepository starRepository;
    private final ConstellationRepository constellationRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;

    public StarServiceImpl(StarRepository starRepository, ConstellationRepository constellationRepository, Gson gson, ModelMapper modelMapper, ValidationUtil validationUtil) {
        this.starRepository = starRepository;
        this.constellationRepository = constellationRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
    }

    @Override
    public boolean areImported() {
        return starRepository.count() > 0;
    }

    @Override
    public String readStarsFileContent() throws IOException {
        return Files.readString(Path.of(FILE_PATH));
    }

    @Override
    public String importStars() throws IOException {
        StringBuilder sb = new StringBuilder();

        StarSeedDto[] starSeedDtos = gson
                .fromJson(readStarsFileContent(), StarSeedDto[].class);


        for (StarSeedDto starSeedDto : starSeedDtos) {
            sb.append(System.lineSeparator());

            if (starRepository.findFirstByName(starSeedDto.getName()).isPresent()
                || !validationUtil.isValid(starSeedDto)){
                sb.append("Invalid star");
            } else {
                sb.append(String.format("Successfully imported star %s - %.2f light years",
                        starSeedDto.getName(),
                        starSeedDto.getLightYears()));
                Long constellationId = starSeedDto.getConstellation();
                Star starToSave = modelMapper.map(starSeedDto, Star.class);
                starToSave.setConstellation(constellationRepository.findAllById(constellationId).orElse(null));

                starRepository.save(starToSave);
            }
        }


        return sb.toString().trim();
    }

    @Override
    public String exportStars() {
        StringBuilder sb = new StringBuilder();

            List<Star> stars = starRepository
                    .findAllStarsRedGiantsAndNeverBeenObservedOrderByLightYears();

        for (Star star : stars) {
            sb.append(System.lineSeparator());


            sb
                    .append(String.format("Star: %s", star.getName()))
                    .append(System.lineSeparator())
                    .append(String.format("   *Distance: %.2f light years", star.getLightYears()))
                    .append(System.lineSeparator())
                    .append(String.format("   **Description: %s", star.getDescription()))
                    .append(System.lineSeparator())
                    .append(String.format("   ***Constellation: %s", star.getConstellation().getName()));

        }


        return sb.toString().trim();
    }
}
