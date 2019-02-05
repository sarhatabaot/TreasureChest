package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.CuboidRegion;

public class RegionIterator {

	private int x;
	private int y;
	private int z;
	
	private int size;
	private Vector3 min;
	private Vector3 max;

	public RegionIterator(Vector3 min, Vector3 max, int subregionSize) {
		this.size = subregionSize;
		this.min = min;
		this.max = max;
		
		reset();
	}
	
	public void reset() {

		x = min.toBlockPoint().getBlockX();
		y = min.toBlockPoint().getBlockY();
		z = min.toBlockPoint().getBlockZ();
		
	}

	public CuboidRegion next() {
		
		if(y > max.toBlockPoint().getBlockY()) {
			reset();
			return null;
		}
		

		CuboidRegion result = createRegion();
		
		if(x + size > max.toBlockPoint().getBlockX()) {
			x = min.toBlockPoint().getBlockX();
			if(z + size > max.toBlockPoint().getBlockZ()) {
				z = min.toBlockPoint().getBlockZ();
				if(y + size > max.toBlockPoint().getBlockY()) {
					// will stop next time
					y+=size;
					return result;
				}
				else {
					y+=size;
				}
			}
			else {
				z+=size;
			}
		}
		else {
			x+=size;
		}
		return result;
	}

	private CuboidRegion createRegion() {
		
		int newX = x;
		int newY = y;
		int newZ = z;
		
		Vector3 minimum = Vector3.at(newX, newY, newZ);
		
		newX += size;
		newY += size;
		newZ += size;
		
		if(newX > max.toBlockPoint().getBlockX()) {
			newX = max.toBlockPoint().getBlockX();
		}
		if(newY > max.toBlockPoint().getBlockY()) {
			newY = max.toBlockPoint().getBlockY();
		}
		if(newZ > max.toBlockPoint().getBlockZ()) {
			newZ = max.toBlockPoint().getBlockZ();
		}
		
		Vector3 maximum = Vector3.at(newX, newY, newZ);
		
		return new CuboidRegion(minimum.toBlockPoint(), maximum.toBlockPoint());
	}
}
