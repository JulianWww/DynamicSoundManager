package net.denanu.dynamicsoundmanager.groups;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;

import fi.dy.masa.malilib.util.FileUtils;
import net.denanu.dynamicsoundmanager.utils.FileModificationUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.util.Identifier;
import net.minecraft.util.WorldSavePath;

public class ClientSoundGroupManager {
	public static ArrayList<Identifier> soundIds = new ArrayList<>();

	public static FileSynchronizationMetadataBuilder metadata;

	public static String getServerName(final MinecraftClient client) {
		final ServerInfo data = client.getCurrentServerEntry();
		if (client.isInSingleplayer()) {
			return "localhost:" + client
					.getServer()
			.getSavePath(WorldSavePath.ROOT)
			.getParent()
			.getFileName()
			.toString();
		}
		return new StringBuilder()
				.append("remote")
				.append(":")
				.append(data.name)
				.append(":")
				.append(data.address)
				.toString();
	}

	public static Path getChach(final MinecraftClient client) {
		Path cachePath =  FileUtils.getConfigDirectory().toPath().resolve("dynamic_sounds");

		cachePath = cachePath.resolve(ClientSoundGroupManager.getServerName(client));
		final File cacheFile = cachePath.toFile();

		FileModificationUtils.mkdirIfAbsent(cacheFile);

		return cachePath;
	}

	public static void setup() {
		final File path = ClientSoundGroupManager.getChach(MinecraftClient.getInstance()).resolve("metadata.json").toFile();
		ClientSoundGroupManager.metadata = new FileSynchronizationMetadataBuilder(path);
	}
}
