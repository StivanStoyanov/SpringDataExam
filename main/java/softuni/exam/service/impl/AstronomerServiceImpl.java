package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.AstronomerRootDto;
import softuni.exam.models.dto.AstronomerSeedDto;
import softuni.exam.models.entity.Astronomer;
import softuni.exam.models.entity.Star;
import softuni.exam.repository.AstronomerRepository;
import softuni.exam.repository.StarRepository;
import softuni.exam.service.AstronomerService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class AstronomerServiceImpl implements AstronomerService {
    private final static String FILE_PATH = "src/main/resources/files/xml/astronomers.xml";
    private final AstronomerRepository astronomerRepository;
    private final StarRepository starRepository;

    private final ModelMapper modelMapper;
    private final ValidationUtil validationUtil;
    private final XmlParser xmlParser;

    public AstronomerServiceImpl(AstronomerRepository astronomerRepository, StarRepository starRepository, ModelMapper modelMapper, ValidationUtil validationUtil, XmlParser xmlParser) {
        this.astronomerRepository = astronomerRepository;
        this.starRepository = starRepository;
        this.modelMapper = modelMapper;
        this.validationUtil = validationUtil;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return astronomerRepository.count() > 0;
    }

    @Override
    public String readAstronomersFromFile() throws IOException {
        return Files.readString(Path.of(FILE_PATH));
    }

    @Override
    public String importAstronomers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();

        List<AstronomerSeedDto> astronomerSeedDtos =
                this.xmlParser
                        .fromFile(Path.of(FILE_PATH).toFile(), AstronomerRootDto.class)
                        .getAstronomers();

        for (AstronomerSeedDto astronomerSeedDto : astronomerSeedDtos) {
            sb.append(System.lineSeparator());

            if (!validationUtil.isValid(astronomerSeedDto)
                || astronomerRepository.findFirstByFirstNameAndLastName(astronomerSeedDto.getFirstName()
                    , astronomerSeedDto.getLastName()).isPresent()
                || !starRepository.findFirstById(astronomerSeedDto.getObservingStar()).isPresent()){
                sb.append("Invalid astronomer");
            } else {
                sb.append(String.format("Successfully imported astronomer %s %s - %.2f",
                        astronomerSeedDto.getFirstName(),
                        astronomerSeedDto.getLastName(),
                        astronomerSeedDto.getAverageObservationHours()));
                Star star = starRepository.findFirstById(astronomerSeedDto.getObservingStar()).orElse(null);

                Astronomer astronomerToSave = modelMapper.map(astronomerSeedDto, Astronomer.class);
                astronomerToSave.setObservingStar(star);

                astronomerRepository.save(astronomerToSave);

            }
        }

        return sb.toString().trim();
    }
}
