package org.raml.emitter;

import org.raml.model.MediaType;
import org.raml.model.RamlApi;
import org.raml.model.Resource;
import org.raml.model.ResourceMethod;
import org.raml.utilities.IndentedAppendable;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

public class IndentedAppendableEmitter implements Emitter {

    private final IndentedAppendable writer;

    private IndentedAppendableEmitter(IndentedAppendable writer) {
        this.writer = writer;
    }
    
    public static IndentedAppendableEmitter create(IndentedAppendable appendable) {
        checkNotNull(appendable);
        
        return new IndentedAppendableEmitter(appendable);
    }

    @Override
    public void emit(RamlApi api) throws RamlEmissionException {
        try {
            writeApi(api);
        } catch (IOException e) {
            throw new RamlEmissionException(format("unable to emit api: %s", api), e);
        }
    }

    private void writeApi(RamlApi api) throws IOException {
        writeHeader();
        writeTitle(api.getTitle());
        writeVersion(api.getVersion());
        writeBaseUri(api.getBaseUri());
        writeDefaultMediaType(api.getDefaultMediaType());

        for (Resource resource : api.getResources()) {
            writeResource(resource);
        }
    }

    private void writeDefaultMediaType(MediaType defaultMediaType) throws IOException {
        writer.appendLine(format("mediaType: %s", defaultMediaType.toString()));
    }

    private void writeResource(Resource resource) throws IOException {
        writer.appendLine(format("%s:", resource.getPath()));
        writer.indent();

        for (ResourceMethod method : resource.getMethods()) {
            writeMethod(method);
        }

        for (Resource child : resource.getChildren()) {
            writeResource(child);
        }

        writer.outdent();
    }

    private void writeMethod(ResourceMethod method) throws IOException {
        writer.appendLine(format("%s:", method.getString()));
        writer.indent();

        if (!method.getConsumedMediaTypes().isEmpty()) {
            writeBody(method.getConsumedMediaTypes());
        }

        writer.outdent();
    }

    private void writeBody(List<MediaType> consumedMediaTypes) throws IOException {
        writer.appendLine("body:");
        writer.indent();

        for (MediaType mediaType : consumedMediaTypes) {
            writer.appendLine(format("%s:", mediaType));
        }

        writer.outdent();
    }

    private void writeHeader() throws IOException {
        writer.appendLine("#%RAML 1.0");
    }

    private void writeTitle(String title) throws IOException {
        writer.appendLine(format("title: %s", title));
    }

    private void writeVersion(String version) throws IOException {
        writer.appendLine(format("version: %s", version));
    }

    private void writeBaseUri(String baseUri) throws IOException {
        writer.appendLine(format("baseUri: %s", baseUri));
    }
}
