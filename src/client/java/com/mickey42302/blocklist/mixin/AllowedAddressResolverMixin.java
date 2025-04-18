package com.mickey42302.blocklist.mixin;

import java.util.Optional;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.client.network.Address;
import net.minecraft.client.network.AddressResolver;
import net.minecraft.client.network.AllowedAddressResolver;
import net.minecraft.client.network.RedirectResolver;
import net.minecraft.client.network.ServerAddress;

@Mixin(AllowedAddressResolver.class)
public class AllowedAddressResolverMixin
{
	@Shadow
	@Final
	private AddressResolver addressResolver;
	
	@Shadow
	@Final
	private RedirectResolver redirectResolver;
	

	@Inject(at = @At("HEAD"),
		method = "resolve(Lnet/minecraft/client/network/ServerAddress;)Ljava/util/Optional;",
		cancellable = true)
	public void resolve(ServerAddress address,
		CallbackInfoReturnable<Optional<Address>> cir)
	{
		
		Optional<Address> optionalAddress = addressResolver.resolve(address);
		Optional<ServerAddress> optionalRedirect =
			redirectResolver.lookupRedirect(address);
		
		if(optionalRedirect.isPresent())
			optionalAddress = addressResolver.resolve(optionalRedirect.get());
		
		cir.setReturnValue(optionalAddress);
	}
}
