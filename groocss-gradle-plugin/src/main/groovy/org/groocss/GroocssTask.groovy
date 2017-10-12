package org.groocss

import org.gradle.api.internal.file.CopyActionProcessingStreamAction
import org.gradle.api.internal.file.FileResolver
import org.gradle.api.internal.file.copy.CopyAction
import org.gradle.api.internal.file.copy.CopyActionProcessingStream
import org.gradle.api.internal.file.copy.FileCopyDetailsInternal
import org.gradle.api.internal.tasks.SimpleWorkResult
import org.gradle.api.tasks.AbstractCopyTask
import org.gradle.api.tasks.WorkResult

/** Converts GrooCSS files using the same features as the Copy task. */
class GroocssTask extends AbstractCopyTask {

    Config conf

    @Override
    protected CopyAction createCopyAction() {
        return new CopyAction() {
            @Override
            WorkResult execute(CopyActionProcessingStream stream) {
                if (!conf) {
                    conf = new Config()
                }
                def action = new GroocssFileAction(fileResolver, conf)
                stream.process(action)
                return new SimpleWorkResult(action.didWork)
            }
        }
    }

    /** Converts the given GrooCSS file with current config, and outputs to given file. */
    static class GroocssFileAction implements CopyActionProcessingStreamAction {
        FileResolver fileResolver
        Config config
        boolean didWork

        GroocssFileAction(FileResolver fileResolver, Config config) {
            this.fileResolver = fileResolver
            this.config = config
        }

        @Override
        void processFile(FileCopyDetailsInternal details) {
            File source = fileResolver.resolve(details.relativeSourcePath.pathString)
            File target = fileResolver.resolve(details.relativePath.pathString)

            if (source.isFile() || source.isDirectory()) {
                GrooCSS.convert(config, source, target)
                didWork = true
            }
        }
    }
}
