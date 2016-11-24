package org.raml.emitter;

import org.raml.model.RamlApi;
import org.raml.utilities.IndentedAppendable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class FileEmitter implements Emitter {

    private static final Logger logger = LoggerFactory.getLogger(FileEmitter.class);

    private final Path filePath;

    private FileEmitter(Path filePath) {
        this.filePath = filePath;
    }

    public static FileEmitter forFile(Path pathToFile) {
        checkNotNull(pathToFile);

        return new FileEmitter(pathToFile);
    }

    @Override
    public void emit(RamlApi api) throws RamlEmissionException {
        if (Files.isRegularFile(filePath)) {
            logger.warn("output file {} already exists, will be overwritten", filePath);
        }

        try (PrintWriter writer = printWriterOf(filePath)) {
            IndentedAppendable appendable = IndentedAppendable.forNoSpaces(4, writer);

            IndentedAppendableEmitter innerEmitter = IndentedAppendableEmitter.create(appendable);

            innerEmitter.emit(api);
        } catch (IOException | RamlEmissionException e) {
            throw new RamlEmissionException(format("unable to successfully output raml to %s", filePath), e);
        }
    }

    private static PrintWriter printWriterOf(Path path) throws IOException {
        return new PrintWriter(Files.newBufferedWriter(path, StandardCharsets.UTF_8));
    }
}
