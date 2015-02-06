package cse520.homework1;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.util.AttributeSet;

import cse520.homework1.R;

public class MainActivity extends Activity {
	
	private TouchSurfaceView mGLView;
	Button button1;
	Button button2;
	Button button3;
	Button button4;
	Button button5;
	Button button6;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mGLView = (TouchSurfaceView) findViewById(R.id.touchsurfaceview);
		addListenerOnButton();

	}

	@Override
	protected void onResume() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onPause() {
		// Ideally a game should implement onResume() and onPause()
		// to take appropriate action when the activity looses focus
		super.onPause();
		mGLView.onPause();
	}
	
	public void addListenerOnButton() {
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		button3 = (Button) findViewById(R.id.button3);
		button4 = (Button) findViewById(R.id.button4);
		button5 = (Button) findViewById(R.id.button5);
		button6 = (Button) findViewById(R.id.button6);

		// cube button
		button1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.tetraButton = false;
				mGLView.icosButton = false;
				mGLView.dodecButton = false;
			}
		});
		
		// tetra button
		button2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.tetraButton = true;
				mGLView.icosButton = false;
				mGLView.dodecButton = false;
			}
		});
		// icosa button
		button3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.tetraButton = false;
				mGLView.icosButton = true;
				mGLView.dodecButton = false;
			}
		});
		
		// dodeca button
		button4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.tetraButton = false;
				mGLView.icosButton = false;
				mGLView.dodecButton = true;
			}
		});
		
		// auto button
		button5.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.autoRotate = true;
			}
		});
		
		// manual button
		button6.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				mGLView.autoRotate = false;
			}
		});

	}
}

class TouchSurfaceView extends GLSurfaceView {

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private final float TRACKBALL_SCALE_FACTOR = 36.0f;
	private float mPreviousX;
	private float mPreviousY;
	// booleans to determine the object to draw
	public boolean tetraButton;
	public boolean icosButton;
	public boolean dodecButton;
	//bolean to determine rotation type
	public boolean autoRotate;
	// variables for rotation
	public float anglex = 0;
	public float angley = 0;
	public float anglez = 0;
	
	//instantiate renderer
	private myRenderer myRenderer = new myRenderer();
		
	// constructor
	public TouchSurfaceView(Context context, AttributeSet attribs) {
		super(context, attribs);
		setRenderer(myRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	// touchscreen function
	@Override
	public boolean onTrackballEvent(MotionEvent e) {
		// manual rotation disables touchscreen rotation
		if (!autoRotate) {
			anglex += e.getX() * TRACKBALL_SCALE_FACTOR;
			anglez += e.getY() * TRACKBALL_SCALE_FACTOR;
		}
		requestRender();
		return true;
	}
	
	// touchscreen function
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			float dx = x - mPreviousX;
			float dy = y - mPreviousY;
			// manual rotation disables touchscreen rotation
			if (!autoRotate) {
				angley += dx * TOUCH_SCALE_FACTOR;
				anglex += dy * TOUCH_SCALE_FACTOR;
			}
			requestRender();
		}
		mPreviousX = x;
		mPreviousY = y;
		return true;
	}

	// renderer
	public class myRenderer implements Renderer {
		Cube cube = new Cube();
		Tetrahedron tetrahedron = new Tetrahedron();
		Icosahedron icosahedron = new Icosahedron();
		Dodecahedron dodecahedron = new Dodecahedron();

		public void onDrawFrame(GL10 gl) {
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glTranslatef(0.0f, 0.0f, -3.0f);
			gl.glRotatef(anglex, 1.0f, 0.0f, 0.0f);
			gl.glRotatef(angley, 0.0f, 1.0f, 0.0f);
			gl.glRotatef(anglez, 0.0f, 0.0f, 1.0f);
			// controls to determine which object to draw depending on button clicked
			if	(tetraButton)
				tetrahedron.draw(gl);
			else if (dodecButton)
				dodecahedron.draw(gl);
			else if (icosButton)
				icosahedron.draw(gl);
			else{
				cube.draw(gl);
				// color array in here to allow setColor to work properly
				// for other objects
			    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			}
			// automatic rotation
			if (autoRotate){
				anglex += 1.0f;
				angley += 1.0f;
				anglez += 1.0f;
			}
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
			requestRender();
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			gl.glViewport(0, 0, width, height);
			float ratio = (float) width / height;
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glDisable(GL10.GL_DITHER);
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
			gl.glClearColor(1, 1, 1, 0);
			gl.glEnable(GL10.GL_CULL_FACE);
			gl.glShadeModel(GL10.GL_SMOOTH);
			gl.glEnable(GL10.GL_DEPTH_TEST);
		}
	}

	
	// Cube object
	public class Cube {
		private FloatBuffer vertexBuffer;
		private FloatBuffer colorBuffer;
		private ByteBuffer indexBuffer;
		
		// Coordinates of 8 vertices of 6 cube faces
		private float vertices[] = { 
				-1.0f, -1.0f, -1.0f, 
				1.0f, -1.0f, -1.0f, 
				1.0f, 1.0f, -1.0f, 
				-1.0f, 1.0f, -1.0f, 
				-1.0f, -1.0f, 1.0f, 
				1.0f, -1.0f, 1.0f, 
				1.0f, 1.0f, 1.0f, 
				-1.0f, 1.0f, 1.0f };
		
		// Colors of vertices
		private float colors[] = { 
				0.0f, 1.0f, 0.0f, 1.0f, 
				0.0f, 1.0f, 0.0f, 1.0f,
				1.0f, 0.5f, 0.0f, 1.0f, 
				1.0f, 0.5f, 0.0f, 1.0f, 
				1.0f, 0.0f, 0.0f, 1.0f, 
				1.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f, 
				1.0f, 0.0f, 1.0f, 1.0f };

		// indices of 12 triangles (6 squares) in GL_CCW
		// referencing vertices[] array coordinates
		private byte indices[] = { 
				5, 4, 0, 
				1, 5, 0, 
				6, 5, 1, 
				2, 6, 1, 
				7, 6, 2, 
				3, 7, 2, 
				4, 7, 3, 
				0, 4, 3, 
				6, 7, 4, 
				5, 6, 4, 
				1, 0, 3, 
				2, 1, 3 };

		public Cube() {
			// initialize vertex Buffer for cube
			// argument=(# of coordinate values * 4 bytes per float)
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			// create a floating point buffer from the ByteBuffer
			vertexBuffer = byteBuf.asFloatBuffer();
			// add the vertices coordinates to the FloatBuffer
			vertexBuffer.put(vertices);
			// set the buffer to read the first vertex coordinates
			vertexBuffer.position(0);
			// Do the same to colors array
			byteBuf = ByteBuffer.allocateDirect(colors.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			colorBuffer = byteBuf.asFloatBuffer();
			colorBuffer.put(colors);
			colorBuffer.position(0);
			// indices are integers
			indexBuffer = ByteBuffer.allocateDirect(indices.length);
			indexBuffer.put(indices);
			indexBuffer.position(0);
		}

		// Typical drawing routine using vertex array
		public void draw(GL10 gl) {
			// Counterclockwise order for front face vertices
			gl.glFrontFace(GL10.GL_CCW);
			// Points to the vertex buffers
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
			// Enable client states
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			// Draw vertices as triangles
			gl.glDrawElements(GL10.GL_TRIANGLES, 36, GL10.GL_UNSIGNED_BYTE, indexBuffer);
			// Disable client state
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}
	
	// Tetrahedron object
	public class Tetrahedron {
		private FloatBuffer vertexBuffer; // buffer holding vertices
		private ByteBuffer[] faceIndexBuffer = new ByteBuffer[4]; // buffer holding faces
		private final int nfaces = 4; // number of faces in object

		float vertices[] = new float[] { 
				1, 0, -1, 
				0, 1, 0, 
				0, -1, 0, 
				1, 0, 0 };

		byte faceIndices[][] = { 
				{ 1, 3, 2 }, 
				{ 0, 1, 2 }, 
				{ 0, 2, 3 }, 
				{ 0, 3, 1 } };

		public Tetrahedron() {
			// a float has 4 bytes so we allocate for each coordinate 4 bytes
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.vertices.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexBuffer = byteBuffer.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			for (int i = 0; i < nfaces; i++) {
				faceIndexBuffer[i] = ByteBuffer
						.allocateDirect(this.faceIndices[i].length);
				faceIndexBuffer[i].put(faceIndices[i]);
				faceIndexBuffer[i].position(0);
			}
		}

		private void setColor(GL10 gl, int i) {
			float R = (float) (i % 3) / 3;
			float G = (float) (i % 4) / 4;
			float B = (float) (i % 5) / 5;
			gl.glColor4f(R, G, B, 0);

		}

		public void draw(GL10 gl) {
			gl.glFrontFace(GL10.GL_CW);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			for (int i = 0; i < nfaces; i++) {
				setColor(gl, i);
				gl.glDrawElements(GL10.GL_TRIANGLES, faceIndexBuffer[i].limit(),
						GL10.GL_UNSIGNED_BYTE, faceIndexBuffer[i]);
			}
		}
	}
	
	// Icosahedron object
	public class Icosahedron {
		private FloatBuffer vertexBuffer; // buffer holding vertices
		private ByteBuffer[] faceIndexBuffer = new ByteBuffer[20]; // buffer holding faces
		private final int nfaces = 20; // number of faces in object

		float a = 0.525731112119133606f;
		float b = 0.850650808352039932f;

		// Vertices
		float vertices[] = new float[] { 
			   -a, 0.0f, b,
			   a, 0.0f, b,
			   -a, 0.0f, -b,
			   a, 0.0f, -b,
			   0.0f, b, a,
			   0.0f, b, -a,
			   0.0f, -b, a,
			   0.0f, -b, -a,
			   b, a, 0.0f,
			   -b, a, 0.0f,
			   b, -a, 0.0f,
			   -b, -a, 0.0f 
		};

		// Faces
		byte faceIndices[][] = { 
				{ 0, 4, 1 }, 
				{ 0, 9, 4 }, 
				{ 9, 5, 4 },
				{ 4, 5, 8 }, 
				{ 4, 8, 1 }, 
				{ 8, 10, 1 }, 
				{ 8, 3, 10 }, 
				{ 5, 3, 8 },
				{ 5, 2, 3 }, 
				{ 2, 7, 3 }, 
				{ 7, 10, 3 }, 
				{ 7, 6, 10 },
				{ 7, 11, 6 },
				{ 11, 0, 6 }, 
				{ 0, 1, 6 }, 
				{ 6, 1, 10 }, 
				{ 9, 0, 11 },
				{ 9, 11, 2 }, 
				{ 9, 2, 5 }, 
				{ 7, 2, 11 }
		};

		public Icosahedron() {
			// a float has 4 bytes so we allocate for each coordinate 4 bytes
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.vertices.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexBuffer = byteBuffer.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			for (int i = 0; i < nfaces; i++) {
				faceIndexBuffer[i] = ByteBuffer.allocateDirect(this.faceIndices[i].length);
				faceIndexBuffer[i].put(faceIndices[i]);
				faceIndexBuffer[i].position(0);
			}
		}

		private void setColor(GL10 gl, int i) {
			float R = (float) (i % 3) / 3;
			float G = (float) (i % 4) / 4;
			float B = (float) (i % 5) / 5;
			gl.glColor4f(R, G, B, 0);
		}

		public void draw(GL10 gl) {
			gl.glFrontFace(GL10.GL_CW);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			for (int i = 0; i < nfaces; i++) {
				setColor(gl, i);
				gl.glDrawElements(GL10.GL_TRIANGLES, faceIndexBuffer[i].limit(),
						GL10.GL_UNSIGNED_BYTE, faceIndexBuffer[i]);
			}
		}
	}
	
	// Dodecahedron object
	public class Dodecahedron {
		private FloatBuffer vertexBuffer; // buffer holding vertices
		private ByteBuffer[] faceIndexBuffer = new ByteBuffer[12]; // buffer holding faces

		private final int nfaces = 12; // number of faces in object
		
		// Vertices
		float vertices[] = new float[] {
				  0.0f,0.847214f,0.323607f,
				  0.0f,0.847214f,-0.323607f,
				  -0.523607f,0.523607f,0.523607f,
				  -0.523607f,0.523607f,-0.523607f,
				  -0.847214f,0.323607f,-0.0f,
				  0.523607f,0.523607f,-0.523607f,
				  0.523607f,0.523607f,0.523607f,
				  0.847214f,0.323607f,-0.0f,
				  0.323607f,-0.0f,-0.847214f,
				  -0.323607f,-0.0f,-0.847214f,
				  -0.323607f,0.0f,0.847214f,
				  0.323607f,0.0f,0.847214f,
				  -0.523607f,-0.523607f,-0.523607f,
				  -0.847214f,-0.323607f,0.0f,
				  -0.523607f,-0.523607f,0.523607f,
				  0.523607f,-0.523607f,0.523607f,
				  0.847214f,-0.323607f,0.0f,
				  0.523607f,-0.523607f,-0.523607f,
				  0.0f,-0.847214f,-0.323607f,
				  0.0f,-0.847214f,0.323607f };
		  
		// Faces
		byte faceIndices[][] = {
				  {0,1,2, 1,3,2, 3,4,2},
				  {1,0,5, 0,6,5, 6,7,5},
				  {1,5,3, 5,8,3, 8,9,3},
				  {0,2,6, 2,10,6, 10,11,6},
				  {3,9,4, 9,12,4, 12,13,4},
				  {2,4,10, 4,13,10, 13,14,10},
				  {6,11,7, 11,15,7, 15,16,7},
				  {5,7,8, 7,16,8, 16,17,8},
				  {9,8,12, 8,17,12, 17,18,12},
				  {11,10,15, 10,14,15, 14,19,15},
				  {13,12,14, 12,18,14, 18,19,14},
				  {16,15,17, 15,19,17, 19,18,17} };

		public Dodecahedron() {
			// a float has 4 bytes so we allocate for each coordinate 4 bytes
			ByteBuffer byteBuffer = ByteBuffer.allocateDirect(this.vertices.length * 4);
			byteBuffer.order(ByteOrder.nativeOrder());
			vertexBuffer = byteBuffer.asFloatBuffer();
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);
			for (int i = 0; i < nfaces; i++) {
				faceIndexBuffer[i] = ByteBuffer.allocateDirect(this.faceIndices[i].length);
				faceIndexBuffer[i].put(faceIndices[i]);
				faceIndexBuffer[i].position(0);
			}
		}

		private void setColor(GL10 gl, int i) {
			float R = (float) (i % 3) / 3;
			float G = (float) (i % 4) / 4;
			float B = (float) (i % 5) / 5;
			gl.glColor4f(R, G, B, 0);

		}

		public void draw(GL10 gl) {
			gl.glFrontFace(GL10.GL_CW);
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
			for (int i = 0; i < nfaces; i++) {
				setColor(gl, i);
				gl.glDrawElements(GL10.GL_TRIANGLES, faceIndexBuffer[i].limit(),
						GL10.GL_UNSIGNED_BYTE, faceIndexBuffer[i]);
			}
		}
	}
}