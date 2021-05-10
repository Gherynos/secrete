/*
 * Copyright (C) 2015-2021  Luca Zanconato (<github.com/gherynos>)
 *
 * This file is part of Secrete.
 *
 * Secrete is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Secrete is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Secrete.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.nharyes.secrete.actions;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;

import net.nharyes.secrete.curve.Curve25519PublicKey;
import net.nharyes.secrete.ecies.ECIESHelper;
import net.nharyes.secrete.ecies.ECIESException;
import net.nharyes.secrete.ecies.ECIESMessage;

import org.apache.commons.cli.CommandLine;

public class EncryptAction extends Action {  // NOPMD

    @Override
    public void execute(CommandLine line, SecureRandom random) throws ActionException {

        try {

            // read data
            Object data = readData(line.getOptionValue('i'), "message");

            // load public key
            String keyToLoad = DEFAULT_PUBLIC_KEY;
            if (line.hasOption('k')) {

                keyToLoad = line.getOptionValue('k');
            }

            try (InputStream fin = Files.newInputStream(Paths.get(keyToLoad))) {

                Curve25519PublicKey key = Curve25519PublicKey.deserialize(fin);

                // encrypt message
                ECIESMessage message;
                if (line.hasOption('i')) {

                    message = ECIESHelper.encryptData(key, (byte[]) data, random);

                } else {

                    message = ECIESHelper.encryptData(key, (String) data, random);
                }

                // write message
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                message.serialize(bout);
                writeData(bout.toByteArray(), line.getOptionValue('o'), true);
            }

        } catch (IOException | ECIESException ex) {

            // re-throw exception
            throw new ActionException(ex.getMessage(), ex);
        }
    }
}
