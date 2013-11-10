package csci582_hw5;

import javax.vecmath.Point3f;

public class Sphere {
	public Point3f center;
	public float radius;
	
	public Sphere() {
		center = new Point3f();
		radius = 0;
	}
	
	public void copyTo(Sphere other) {
		other.radius = this.radius;
		other.center.x = this.center.x;
		other.center.y = this.center.y;
		other.center.z = this.center.z;
	}
	
	public Sphere union(Sphere other) {
		Sphere result = new Sphere();
		if(this.radius == 0.0) {
			other.copyTo(result);
			return result;
		}
		if(other.radius == 0.0) {
			this.copyTo(result);
			return result;
		}
		float distance = this.center.distance(other.center);
		float radius = (distance + this.radius + other.radius)/2.0f;
		if(distance < Math.abs(this.radius-other.radius)) {
			if(this.radius > other.radius)
				this.copyTo(result);
			else
				other.copyTo(result);
				
			return result;
		}
		
		float u_l = -this.radius/distance;
		float u_r = 1+other.radius/distance;
		float vec_l[] = {
				this.center.x*(1-u_l) + other.center.x*u_l,
				this.center.y*(1-u_l) + other.center.y*u_l,
				this.center.z*(1-u_l) + other.center.z*u_l,
		};
		float vec_r[] = {
				this.center.x*(1-u_r) + other.center.x*u_r,
				this.center.y*(1-u_r) + other.center.y*u_r,
				this.center.z*(1-u_r) + other.center.z*u_r,
		};
		result.radius =radius;
		result.center.x = (vec_l[0]+vec_r[0])/2.0f;
		result.center.y = (vec_l[1]+vec_r[1])/2.0f;
		result.center.z = (vec_l[2]+vec_r[2])/2.0f;
		
		return result;
	}
	
	public Sphere intersect(Sphere other) {
		Sphere result = new Sphere();
		if(this.radius == 0.0f || other.radius == 0.0f)
			return result;
		float d = this.center.distance(other.center);
		float r1 = this.radius;
		float r2 = other.radius;
		if(d >= r1+r2)
			return result;
		
		float cos = (r1*r1 + d*d - r2*r2)/(2.0f * d* r1);
		float sin = (float)Math.sqrt(1.0-cos*cos);
		result.radius = sin * r1;
		float vec[] = {
				(other.center.x - this.center.x)/d,
				(other.center.y - this.center.y)/d,
				(other.center.z - this.center.z)/d
		};
		float temp = r1*cos;
		result.center.x = this.center.x + vec[0] * temp;
		result.center.y = this.center.y + vec[1] * temp;
		result.center.z = this.center.z + vec[2] *temp;

		return result;
	}
}