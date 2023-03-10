package net.denanu.dynamicsoundmanager.player_api;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import javax.sound.sampled.AudioFormat;

import net.denanu.dynamicsoundmanager.DynamicSoundManager;
import net.denanu.dynamicsoundmanager.groups.client.ClientSoundGroupManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.OggAudioStream;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.StaticSound;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.floatprovider.ConstantFloatProvider;

@Environment(value=EnvType.CLIENT)
public class DynamicSound extends Sound {
	private final DynamicSoundConfigs config;

	public static final String SHOULD_LOAD_DYNAMICLY = DynamicSoundManager.MOD_ID + ".dynamic_loader";

	public DynamicSound(final DynamicSoundConfigs config) {
		super(
				config.getId().toString(),
				ConstantFloatProvider.create(config.getVolume()),
				ConstantFloatProvider.create(config.getPitch()),
				config.getWeight(),
				Sound.RegistrationType.FILE,
				config.getStream(),
				config.getPreload(),
				config.getAttenuation()
				);
		this.config = config;
	}

	@Override
	public Identifier getLocation() {
		return new Identifier(
				ClientSoundGroupManager.getChach(MinecraftClient.getInstance()).toAbsolutePath().toString()
				+ "/"
				+ this.getIdentifier().getNamespace()
				+ "/"
				+ this.getIdentifier().getPath()
				+ "/"
				+ this.config.getKey(),
				DynamicSound.SHOULD_LOAD_DYNAMICLY);
	}

	public String getKey() {
		return this.config.getKey();
	}

	public static CompletableFuture<StaticSound> loadDynamicSound(final Identifier id) {
		return CompletableFuture.supplyAsync(() -> {
			try (InputStream inputStream = new FileInputStream(id.getNamespace())){
				StaticSound staticSound;
				try (OggAudioStream oggAudioStream = new OggAudioStream(inputStream);){
					final ByteBuffer byteBuffer = oggAudioStream.getBuffer();

					final AudioFormat format = oggAudioStream.getFormat();
					format.getFrameSize();
					format.getFrameRate();
					byteBuffer.limit();

					staticSound = new StaticSound(byteBuffer, oggAudioStream.getFormat());
				}
				return staticSound;
			}
			catch (final IOException iOException) {
				throw new CompletionException(iOException);
			}
		}, Util.getMainWorkerExecutor());
	}

	public DynamicSoundConfigs getConfig() {
		return this.config;
	}
}
