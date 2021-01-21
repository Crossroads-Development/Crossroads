package com.Da_Technomancer.crossroads.API;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Stripped down re-implementation of net.minecraft.util.math.vector.Quaternion that is server-side safe
 */
public class ServerQuaternion{

	private static final ServerQuaternion ONE = new ServerQuaternion(0.0F, 0.0F, 0.0F, 1.0F);

	private float x;
	private float y;
	private float z;
	private float w;

	public static ServerQuaternion getOne(){
		return ONE.copy();
	}

	public ServerQuaternion(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public ServerQuaternion(Vector3f axis, float angle, boolean degrees){
		if(degrees){
			angle *= ((float) Math.PI / 180F);
		}

		float f = sin(angle / 2.0F);
		this.x = axis.getX() * f;
		this.y = axis.getY() * f;
		this.z = axis.getZ() * f;
		this.w = cos(angle / 2.0F);
	}

	public ServerQuaternion(float xAngle, float yAngle, float zAngle, boolean degrees){
		if(degrees){
			xAngle *= ((float) Math.PI / 180F);
			yAngle *= ((float) Math.PI / 180F);
			zAngle *= ((float) Math.PI / 180F);
		}

		float f = sin(0.5F * xAngle);
		float f1 = cos(0.5F * xAngle);
		float f2 = sin(0.5F * yAngle);
		float f3 = cos(0.5F * yAngle);
		float f4 = sin(0.5F * zAngle);
		float f5 = cos(0.5F * zAngle);
		this.x = f * f3 * f5 + f1 * f2 * f4;
		this.y = f1 * f2 * f5 - f * f3 * f4;
		this.z = f * f2 * f5 + f1 * f3 * f4;
		this.w = f1 * f3 * f5 - f * f2 * f4;
	}

	public ServerQuaternion(Quaternion quaternionIn){
		this.x = quaternionIn.getX();
		this.y = quaternionIn.getY();
		this.z = quaternionIn.getZ();
		this.w = quaternionIn.getW();
	}

	public float getX(){
		return this.x;
	}

	public float getY(){
		return this.y;
	}

	public float getZ(){
		return this.z;
	}

	public float getW(){
		return this.w;
	}

	public void multiply(ServerQuaternion quaternionIn){
		float f = this.getX();
		float f1 = this.getY();
		float f2 = this.getZ();
		float f3 = this.getW();
		float f4 = quaternionIn.getX();
		float f5 = quaternionIn.getY();
		float f6 = quaternionIn.getZ();
		float f7 = quaternionIn.getW();
		this.x = f3 * f4 + f * f7 + f1 * f6 - f2 * f5;
		this.y = f3 * f5 - f * f6 + f1 * f7 + f2 * f4;
		this.z = f3 * f6 + f * f5 - f1 * f4 + f2 * f7;
		this.w = f3 * f7 - f * f4 - f1 * f5 - f2 * f6;
	}

	public void multiply(float valueIn){
		this.x *= valueIn;
		this.y *= valueIn;
		this.z *= valueIn;
		this.w *= valueIn;
	}

	public void conjugate(){
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public void set(float x, float y, float z, float w){
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	private static float cos(float p_214904_0_){
		return (float) Math.cos((double) p_214904_0_);
	}

	private static float sin(float p_214903_0_){
		return (float) Math.sin((double) p_214903_0_);
	}

	public void normalize(){
		float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
		if(f > 1.0E-6F){
			float f1 = MathHelper.fastInvSqrt(f);
			this.x *= f1;
			this.y *= f1;
			this.z *= f1;
			this.w *= f1;
		}else{
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.w = 0.0F;
		}
	}

	public ServerQuaternion copy(){
		return new ServerQuaternion(x, y, z, w);
	}

	public Quaternion toQuaternion(){
		return new Quaternion(x, y, z, w);
	}

	@Override
	public boolean equals(Object o){
		if(this == o){
			return true;
		}
		if(o == null || getClass() != o.getClass()){
			return false;
		}
		ServerQuaternion that = (ServerQuaternion) o;
		return Float.compare(that.x, x) == 0 &&
				Float.compare(that.y, y) == 0 &&
				Float.compare(that.z, z) == 0 &&
				Float.compare(that.w, w) == 0;
	}

	@Override
	public int hashCode(){
		int i = Float.floatToIntBits(this.x);
		i = 31 * i + Float.floatToIntBits(this.y);
		i = 31 * i + Float.floatToIntBits(this.z);
		return 31 * i + Float.floatToIntBits(this.w);
	}

	@Override
	public String toString(){
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append("ServerQuaternion[").append(getW()).append(" + ");
		stringbuilder.append(getX()).append("i + ");
		stringbuilder.append(getY()).append("j + ");
		stringbuilder.append(getZ()).append("k]");
		return stringbuilder.toString();
	}
}
